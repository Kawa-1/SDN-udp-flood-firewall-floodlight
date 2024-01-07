package pl.edu.agh.kt;

import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.projectfloodlight.openflow.protocol.OFFlowStatsEntry;
import org.projectfloodlight.openflow.protocol.OFFlowStatsReply;
import org.projectfloodlight.openflow.protocol.OFFlowStatsRequest;
import org.projectfloodlight.openflow.protocol.OFPortStatsEntry;
import org.projectfloodlight.openflow.protocol.OFPortStatsReply;
import org.projectfloodlight.openflow.protocol.OFStatsReply;
import org.projectfloodlight.openflow.protocol.OFStatsRequest;
import org.projectfloodlight.openflow.protocol.match.MatchField;
import org.projectfloodlight.openflow.types.OFPort;
import org.projectfloodlight.openflow.types.TableId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.util.concurrent.ListenableFuture;

import net.floodlightcontroller.core.IOFSwitch;

public class StatisticsCollector {
    private static final Logger logger = LoggerFactory.getLogger(StatisticsCollector.class);
    private IOFSwitch sw;

    public class PortStatisticsPoller extends TimerTask {
        private final Logger logger = LoggerFactory.getLogger(PortStatisticsPoller.class);
        private long lastTxPacketCount = 0;
        private long lastRxPacketCount = 0;
        private long lastTxBytesCount = 0;
        private long lastRxBytesCount = 0;
        private long lastTimestamp = System.currentTimeMillis();

        @Override
        public void run() {
            logger.debug("run() begin");
            synchronized (StatisticsCollector.this) {
                if (sw == null) { // no switch
                    logger.error("run() end (no switch)");
                    return;
                }

                ListenableFuture<?> future;
                List<OFStatsReply> values = null;
                OFStatsRequest<?> req = null;
                req = sw.getOFFactory().buildPortStatsRequest().setPortNo(OFPort.ANY).build();

                try {
                    if (req != null) {
                        future = sw.writeStatsRequest(req);
                        values = (List<OFStatsReply>) future.get(PORT_STATISTICS_POLLING_INTERVAL * 1000 / 2, TimeUnit.MILLISECONDS);
                    }
                    OFPortStatsReply psr = (OFPortStatsReply) values.get(0);
                    logger.info("Switch id: {}", sw.getId());
                    long currentTimestamp = System.currentTimeMillis();

                    for (OFPortStatsEntry pse : psr.getEntries()) {
                        if (pse.getPortNo().getPortNumber() > 0) {
                            long currentTxPacketCount = pse.getTxPackets().getValue();
                            long currentRxPacketCount = pse.getRxPackets().getValue();
                            
                            long currentTxBytesCount = pse.getTxBytes().getValue();
                            long currentRxBytesCount = pse.getRxBytes().getValue();

                            double txSpeedP = calculateSpeed(currentTxPacketCount, lastTxPacketCount, currentTimestamp, lastTimestamp);
                            double rxSpeedP = calculateSpeed(currentRxPacketCount, lastRxPacketCount, currentTimestamp, lastTimestamp);
                            
                            double txSpeedB = calculateSpeed(currentTxBytesCount, lastTxBytesCount, currentTimestamp, lastTimestamp) / 1000.0;
                            double rxSpeedB = calculateSpeed(currentRxBytesCount, lastRxBytesCount, currentTimestamp, lastTimestamp) / 1000.0;

//                            logger.debug("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
//                            
//                            logger.debug("$$$$$$$$$$$$$$ Port number: {} $$$$$$$$$$$$$$", pse.getPortNo().getPortNumber());
//                          
//                            logger.debug("$$$ TX packets speed: {} packets/s", Double.toString(txSpeedP));
//                            logger.debug("$$$ TX kbps speed: {} kb/s", Double.toString(txSpeedB));
//                            
//                            logger.debug("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
//                            
//                            logger.debug("$$$ RX packets speed: {} packets/s", Double.toString(rxSpeedP));
//                            logger.debug("$$$ RX kbps speed: {} kb/s", Double.toString(rxSpeedB));
                        }
                    }

                    List<OFFlowStatsEntry> flowStats = getFlowStatistics(sw);
                    for (OFFlowStatsEntry flowStat : flowStats) {                       
                        logger.debug("&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&");
                        logger.debug("&&&&& Flow SRC: {} &&&&&", flowStat.getMatch().get(MatchField.ETH_SRC).toString());
//                        logger.debug("&&&&& Packet Count ID: {} &&&&&", flowStat.getPacketCount().getValue());
//                        logger.debug("&&&&& Byte Count ID: {} &&&&&", flowStat.getByteCount().getValue());
                        logger.debug("&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&");
                    }
                    
                    logger.debug("***********************************************************");
                    
                    lastTxPacketCount = psr.getEntries().get(0).getTxPackets().getValue();
                    lastRxPacketCount = psr.getEntries().get(0).getRxPackets().getValue();
                    lastTxBytesCount = psr.getEntries().get(0).getTxBytes().getValue();
                    lastRxBytesCount = psr.getEntries().get(0).getRxBytes().getValue();
                    lastTimestamp = currentTimestamp;

                } catch (InterruptedException | ExecutionException | TimeoutException ex) {
                    logger.error("Error during statistics polling", ex);
                }
            }
            logger.debug("run() end");
        }

        private double calculateSpeed(long currentCount, long lastCount, long currentTime, long lastTime) {
            double countChange = currentCount - lastCount;
            double timeChange = (currentTime - lastTime) / 1000.0;

            return countChange / timeChange;
        }
    }

    public static final int PORT_STATISTICS_POLLING_INTERVAL = 3000; // in ms

    private static StatisticsCollector singleton;

    private StatisticsCollector(IOFSwitch sw) {
        this.sw = sw;
        new Timer().scheduleAtFixedRate(new PortStatisticsPoller(), 0, PORT_STATISTICS_POLLING_INTERVAL);
    }

    public static StatisticsCollector getInstance(IOFSwitch sw) {
        logger.debug("getInstance() begin");
        synchronized (StatisticsCollector.class) {
            if (singleton == null) {
                logger.debug("Creating StatisticsCollector singleton");
                singleton = new StatisticsCollector(sw);
            }
        }
        logger.debug("getInstance() end");
        return singleton;
    }

    private List<OFFlowStatsEntry> getFlowStatistics(IOFSwitch sw) {
        OFFlowStatsRequest flowStatsRequest = sw.getOFFactory().buildFlowStatsRequest()
                .setMatch(sw.getOFFactory().buildMatch().build())
                .setOutPort(OFPort.ANY)
                .setTableId(TableId.ALL)
                .build();

        try {
            ListenableFuture<List<OFFlowStatsReply>> future = sw.writeStatsRequest(flowStatsRequest);
            List<OFFlowStatsReply> replies = future.get(PORT_STATISTICS_POLLING_INTERVAL * 1000 / 2, TimeUnit.MILLISECONDS);
//            logger.debug("========================= {} ", replies.size());
            if (!replies.isEmpty()) {
                return replies.get(0).getEntries();
            }
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            logger.error("Error retrieving flow statistics", e);
        }

        return Collections.emptyList();
    }
}
