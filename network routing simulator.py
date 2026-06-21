import heapq
import time
import random

class Network:
    def __init__(self):
        self.graph = {}

    def add_router(self, router):
        if router not in self.graph:
            self.graph[router] = {}

    def connect(self, router1, router2, latency):
        self.graph[router1][router2] = latency
        self.graph[router2][router1] = latency

    def dijkstra(self, start, end):
        distances = {node: float('inf') for node in self.graph}
        distances[start] = 0

        previous = {node: None for node in self.graph}

        pq = [(0, start)]

        while pq:
            current_distance, current_node = heapq.heappop(pq)

            if current_node == end:
                break

            if current_distance > distances[current_node]:
                continue

            for neighbor, weight in self.graph[current_node].items():
                distance = current_distance + weight

                if distance < distances[neighbor]:
                    distances[neighbor] = distance
                    previous[neighbor] = current_node
                    heapq.heappush(pq, (distance, neighbor))

        path = []
        current = end

        while current:
            path.append(current)
            current = previous[current]

        path.reverse()

        return path, distances[end]

    def display(self):
        print("\nNETWORK TOPOLOGY")
        print("-" * 40)

        for router in self.graph:
            print(f"{router} -> {self.graph[router]}")


class Packet:
    def __init__(self, source, destination, data):
        self.source = source
        self.destination = destination
        self.data = data

    def transmit(self, network):
        path, total_latency = network.dijkstra(
            self.source,
            self.destination
        )

        print("\nPACKET TRANSMISSION")
        print("-" * 40)
        print(f"Source      : {self.source}")
        print(f"Destination : {self.destination}")
        print(f"Payload     : {self.data}")
        print(f"Best Route  : {' -> '.join(path)}")
        print(f"Latency     : {total_latency} ms\n")

        for i in range(len(path)):
            print(f"Packet at Router {path[i]}")
            time.sleep(0.8)

        print("\nPacket Delivered Successfully!\n")


def create_sample_network():
    network = Network()

    routers = ["A", "B", "C", "D", "E", "F", "G"]

    for router in routers:
        network.add_router(router)

    network.connect("A", "B", 4)
    network.connect("A", "C", 2)
    network.connect("B", "C", 1)
    network.connect("B", "D", 5)
    network.connect("C", "D", 8)
    network.connect("C", "E", 10)
    network.connect("D", "E", 2)
    network.connect("D", "F", 6)
    network.connect("E", "G", 3)
    network.connect("F", "G", 1)

    return network


def generate_random_packet(network):
    routers = list(network.graph.keys())

    source = random.choice(routers)
    destination = random.choice(routers)

    while destination == source:
        destination = random.choice(routers)

    messages = [
        "HTTP Request",
        "Video Stream",
        "Email Data",
        "DNS Lookup",
        "Database Query",
        "File Transfer"
    ]

    payload = random.choice(messages)

    return Packet(source, destination, payload)


def main():
    network = create_sample_network()

    while True:
        print("\nNETWORK PACKET ROUTING SIMULATOR")
        print("=" * 40)
        print("1. Show Network")
        print("2. Send Custom Packet")
        print("3. Send Random Packet")
        print("4. Exit")

        choice = input("\nEnter choice: ")

        if choice == "1":
            network.display()

        elif choice == "2":
            source = input("Source Router: ").upper()
            destination = input("Destination Router: ").upper()
            data = input("Payload: ")

            if source not in network.graph or destination not in network.graph:
                print("Invalid router.")
                continue

            packet = Packet(source, destination, data)
            packet.transmit(network)

        elif choice == "3":
            packet = generate_random_packet(network)
            packet.transmit(network)

        elif choice == "4":
            print("Exiting...")
            break

        else:
            print("Invalid choice.")


if __name__ == "__main__":
    main()