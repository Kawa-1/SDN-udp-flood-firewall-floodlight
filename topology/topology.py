from typing import Dict, List, Union
from mininet.topo import Topo


NodeInfo = Dict[str, Union[List[str], str]]
TopologyDict = Dict[str, NodeInfo]


topology_dict: TopologyDict = {
    "s1": {"connections": ["h1", "h2", "h3", "h4", "h5"]},
    "h1": {"connections": [], "ip": "10.0.0.1"},
    "h2": {"connections": [], "ip": "10.0.0.2"},
    "h3": {"connections": [], "ip": "10.0.0.3"},
    "h4": {"connections": [], "ip": "10.0.0.4"},
    "h5": {"connections": [], "ip": "10.0.0.100"},
}


class CustomTopo(Topo):
    def build(self, topology_dict: TopologyDict = topology_dict):
        nodes = {}
        created_links = []

        for node, node_info in topology_dict.items():
            connections = node_info["connections"]
            ip = node_info.get("ip", None)
            if node.startswith("s"):
                nodes[node] = self.addSwitch(node)
            elif node.startswith("h"):
                nodes[node] = self.addHost(node, ip=ip)
            else:
                raise ValueError(
                    f"{node} - forbidden to add to this topology other node than switch (s) or host (h)"
                )

            for conn in connections:
                link = sorted([node, conn])
                if link not in created_links:
                    if conn not in nodes:
                        if node.startswith("s"):
                            nodes[conn] = self.addSwitch(conn)
                        elif node.startswith("h"):
                            nodes[conn] = self.addHost(
                                conn, ip=topology_dict[conn].get("ip", None)
                            )
                        else:
                            raise ValueError(
                                f"{node} - forbidden to add to this topology other node than switch (s) or host (h)"
                            )
                    self.addLink(nodes[node], nodes[conn])
                    created_links.append(link)


topos = {"mytopo": CustomTopo}
