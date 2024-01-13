from mininet.topo import Topo

topology_dict = {
    "s1": {"connections": ["h1", "h2", "h3", "h4", "h5"]},
    "h1": {"connections": [], "ip": "10.0.0.1", "mac": "6a:87:45:7a:43:11"},
    "h2": {"connections": [], "ip": "10.0.0.2", "mac": "6a:87:45:7a:43:22"},
    "h3": {"connections": [], "ip": "10.0.0.3", "mac": "6a:87:45:7a:43:33"},
    "h4": {"connections": [], "ip": "10.0.0.4", "mac": "6a:87:45:7a:43:44"},
    "h5": {"connections": [], "ip": "10.0.0.5", "mac": "6a:87:45:7a:43:55"},
}

class CustomTopo(Topo):
    def build(self, topology_dict=topology_dict):
        """Build the network topology.

        Creates network nodes (switches and hosts) and links them based on the provided topology dictionary.
        
        Args:
            topology_dict (dict): A dictionary that defines the network topology.
                                  Defaults to the globally defined `topology_dict`.

        Raises:
            ValueError: If an invalid node (not a switch or host) is specified in the topology dictionary.
        """
        nodes = {}
        created_links = []

        for node, node_info in topology_dict.items():
            connections = node_info.get("connections", [])
            ip = node_info.get("ip", None)
            mac = node_info.get("mac", None)
            if node.startswith("s"):
                nodes[node] = self.addSwitch(node)
            elif node.startswith("h"):
                nodes[node] = self.addHost(node, ip=ip, mac=mac)
            else:
                raise ValueError(
                    "{0} - forbidden to add to this topology other node than switch (s) or host (h)".format(node)
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
                                "{0} - forbidden to add to this topology other node than switch (s) or host (h)".format(node)
                            )
                    self.addLink(nodes[node], nodes[conn])
                    created_links.append(link)

topos = {"mytopo": CustomTopo}
