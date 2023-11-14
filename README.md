# SDN UDP Flood Firewall with Floodlight Controller

## Overview

This project implements a UDP flood firewall using the Floodlight controller, an open-source OpenFlow controller, in a Software Defined Network (SDN) environment. The goal is to mitigate UDP flood attacks by dynamically adjusting flow rules in response to network conditions. Based on the number of requests within a specific time frame, the system will block a user attempting a DDoS attack.

## Prerequisites

**in progress**

## Installation

**in progress**

## Configuration

In this topology, a central point is represented by a switch serving as the core. Connected to this switch are four PC terminals, comprising two "legitimate" hosts alongside two "malicious" hosts, intended to simulate attack scenarios. Additionally, the switch interfaces with a controller, while a server is also linked to the switch to complete the network setup.

## Topology:

![](images/topologia.png)

## Documentation:

- [Floodlight OpenFlow Controller (OSS)](https://github.com/floodlight/floodlight#floodlight-openflow-controller-oss)
- 

## Literature:

- **DOS Attack Mitigation Strategies on SDN Controller** by Yun Tian, Vincent Tran, Mutalifu Kuerban (2019) presented at IEEE 9th Annual Computing and Communication Workshop and Conference (CCWC).
- **Extending the Floodlight Controller** by Laura Victoria Morales, Andres Felipe Murillo, Sandra Julieta Rueda presented at the 2015 IEEE 14th International Symposium on Network Computing and Applications.
- **SDN Controllers: A Comparative Study** by Ola Salman, Imad H. Elhajj, Ayman Kayssi, Ali Chehab, presented at the 2016 18th Mediterranean Electrotechnical Conference (MELECON), published by IEEE.
- **Early Detection of Distributed Denial of Service Attack in Era of Software-Defined Network** by Bineet Kumar Joshi, Nitin Joshi, Mahesh Chandra Joshi, presented at the 2018 Eleventh International Conference on Contemporary Computing (IC3), published by IEEE.
- **Early Detection of DDoS Attacks Against SDN Controllers** by Seyed Mohammad Mousavi, Marc St-Hilaire, presented at the 2015 International Conference on Computing, Networking, and Communications (ICNC), published by IEEE.
