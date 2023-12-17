```{java}
List<OFFlowStatsEntry> flowStats = getFlowStatistics(sw);
Map<Integer, int[]> historyFlows = new HashMap<>(); // key - flowId, value [ilość pakietów z poprzedniej iteracji, timestamp do obliczeń]
Map<String, Integer> blockedHosts = new HashMap<>(); // key - adres MAC/IP, value - timestamp kiedy został zablokowany

for (OFFlowStatsEntry flowStat : flowStats) {
    if (wartoscZakomentowana > THRESHOLD) { // (flowStat.getPacketCount().getValue(); - historyFlows[flowId][1]) / (actualTimestamp - historyFlows[flowId][2]) 
    	block(flowId);	// flowId = flowStat.getCookie().getValue()
    }
    
}

// metoda
block(flowId) {
    // match hosta po flowId po adresie MAC lub IP
    // dodawanie go na czarną listę na 60 sekund - każdy ruch z tego hosta jest odrzucany z tego
    // usuń wszystkie przepływy danego hosta
}

receivePacketIn() {
    // sprawdzenie czy dany pakiet przychodzi od hosta znajdującego się na czarnej liście - jeśli tak
    	// sprawdź czy został zablokowany > 60 sekund temu - jeśli tak
    		// odblokuj i zaakeptuj ruch
    	// jeśli nie
    		// dalej blokuj
    // jeśli nie
    	// zaakceptuj ruch
}
```

# Próba implementacji speed

```{java}
import org.projectfloodlight.openflow.protocol.OFFlowStatsEntry;
import org.projectfloodlight.openflow.types.MacAddress;
import org.projectfloodlight.openflow.types.IPAddress;

public class MyModule extends SomeFloodlightModule {

    private static final int THRESHOLD = 100; // Przykładowy próg blokowania

    private Map<Integer, int[]> historyFlows = new HashMap<>(); // key - flowId, value [ilość pakietów z poprzedniej iteracji, timestamp do obliczeń]
    private Map<String, Integer> blockedHosts = new HashMap<>(); // key - adres MAC/IP, value - timestamp kiedy został zablokowany

    // Metoda do przetwarzania statystyk przepływów
    public void processFlowStatistics(List<OFFlowStatsEntry> flowStats) {
        for (OFFlowStatsEntry flowStat : flowStats) {
            int flowId = flowStat.getCookie().getValue();

            if (historyFlows.containsKey(flowId)) {
                int[] flowData = historyFlows.get(flowId);
                int packetCount = flowStat.getPacketCount().getValue();
                int prevPacketCount = flowData[0];
                int prevTimestamp = flowData[1];
                int currentTimestamp = getCurrentTimestamp(); // Implementuj tę metodę

                if ((packetCount - prevPacketCount) / (currentTimestamp - prevTimestamp) > THRESHOLD) {
                    block(flowId);
                }
            }
            // Dodaj aktualne dane przepływu do historii
            int[] data = new int[]{flowStat.getPacketCount().getValue(), getCurrentTimestamp()};
            historyFlows.put(flowId, data);
        }
    }

    // Metoda blokująca hosta na podstawie przepływu
    private void block(int flowId) {
        // Wyszukaj adres MAC lub IP hosta na podstawie flowId
        // Zablokuj hosta na 60 sekund
        // blockedHosts.put(hostAddress, getCurrentTimestamp() + 60); // hostAddress - adres zablokowanego hosta
        // usuń wszystkie przepływy danego hosta
    }

    // Metoda obsługująca otrzymane pakiety
    public void receivePacketIn(MacAddress sourceMac, IPAddress sourceIp) {
        String hostAddress = sourceMac.toString(); // Przykładowo, można użyć MAC jako identyfikatora hosta

        if (blockedHosts.containsKey(hostAddress)) {
            int blockedTimestamp = blockedHosts.get(hostAddress);
            int currentTimestamp = getCurrentTimestamp();

            if (currentTimestamp - blockedTimestamp > 60) {
                unblock(hostAddress);
                acceptPacket(); // Zaakceptuj pakiet
            } else {
                // Kontynuuj blokowanie
                dropPacket(); // Odrzuć pakiet
            }
        } else {
            acceptPacket(); // Zaakceptuj pakiet
        }
    }

    // Metoda odblokowująca hosta
    private void unblock(String hostAddress) {
        blockedHosts.remove(hostAddress);
    }

    // Metoda symulująca akceptację pakietu
    private void acceptPacket() {
        // Logika akceptacji pakietu
    }

    // Metoda symulująca odrzucenie pakietu
    private void dropPacket() {
        // Logika odrzucenia pakietu
    }

    // Metoda do pobierania aktualnego znacznika czasu
    private int getCurrentTimestamp() {
        // Zwróć aktualny znacznik czasu, np. używając System.currentTimeMillis()
        return (int) (System.currentTimeMillis() / 1000);
    }
}

```

