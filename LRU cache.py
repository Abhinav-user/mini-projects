class Node:
    def __init__(self, key, value):
        self.key = key
        self.value = value
        self.prev = None
        self.next = None


class LRUCache:
    def __init__(self, capacity):
        self.capacity = capacity
        self.cache = {}

        # Dummy head and tail nodes
        self.head = Node(0, 0)
        self.tail = Node(0, 0)

        self.head.next = self.tail
        self.tail.prev = self.head

    def _remove(self, node):
        prev_node = node.prev
        next_node = node.next

        prev_node.next = next_node
        next_node.prev = prev_node

    def _insert_at_front(self, node):
        first = self.head.next

        self.head.next = node
        node.prev = self.head

        node.next = first
        first.prev = node

    def get(self, key):
        if key not in self.cache:
            return -1

        node = self.cache[key]

        self._remove(node)
        self._insert_at_front(node)

        return node.value

    def put(self, key, value):
        if key in self.cache:
            node = self.cache[key]
            node.value = value

            self._remove(node)
            self._insert_at_front(node)

        else:
            if len(self.cache) >= self.capacity:

                lru = self.tail.prev

                self._remove(lru)

                del self.cache[lru.key]

            new_node = Node(key, value)

            self.cache[key] = new_node

            self._insert_at_front(new_node)

    def display(self):
        current = self.head.next

        print("Cache State (MRU → LRU):")

        while current != self.tail:
            print(f"[{current.key}:{current.value}]", end=" ")
            current = current.next

        print("\n")


# Demo
if __name__ == "__main__":

    cache = LRUCache(3)

    cache.put(1, "A")
    cache.put(2, "B")
    cache.put(3, "C")

    cache.display()

    print("Get 2:", cache.get(2))
    cache.display()

    cache.put(4, "D")  # Evicts key 1

    cache.display()

    print("Get 1:", cache.get(1))
    print("Get 4:", cache.get(4))

    cache.display()