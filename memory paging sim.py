# ================================
# Memory Paging Simulator
# Algorithms:
#   1. FIFO
#   2. LRU
#   3. Optimal
# ================================

from collections import deque


def print_step(page, frames, fault):
    print(f"Page {page:2} -> {frames} {'FAULT' if fault else 'HIT'}")


# ---------------- FIFO ---------------- #
def fifo(reference, capacity):
    frames = []
    queue = deque()

    faults = 0
    hits = 0

    print("\n========== FIFO ==========")

    for page in reference:

        if page in frames:
            hits += 1
            print_step(page, frames.copy(), False)

        else:
            faults += 1

            if len(frames) < capacity:
                frames.append(page)
                queue.append(page)

            else:
                oldest = queue.popleft()
                idx = frames.index(oldest)
                frames[idx] = page
                queue.append(page)

            print_step(page, frames.copy(), True)

    return faults, hits


# ---------------- LRU ---------------- #
def lru(reference, capacity):

    frames = []
    recent = {}

    faults = 0
    hits = 0

    print("\n========== LRU ==========")

    for i, page in enumerate(reference):

        if page in frames:
            hits += 1
            recent[page] = i
            print_step(page, frames.copy(), False)

        else:
            faults += 1

            if len(frames) < capacity:
                frames.append(page)

            else:
                least_recent = min(frames, key=lambda p: recent[p])
                idx = frames.index(least_recent)
                frames[idx] = page

            recent[page] = i
            print_step(page, frames.copy(), True)

    return faults, hits


# ---------------- Optimal ---------------- #
def optimal(reference, capacity):

    frames = []

    faults = 0
    hits = 0

    print("\n========== OPTIMAL ==========")

    for i, page in enumerate(reference):

        if page in frames:
            hits += 1
            print_step(page, frames.copy(), False)

        else:
            faults += 1

            if len(frames) < capacity:
                frames.append(page)

            else:

                future = reference[i + 1:]
                replace = None
                farthest = -1

                for f in frames:

                    if f not in future:
                        replace = f
                        break

                    index = future.index(f)

                    if index > farthest:
                        farthest = index
                        replace = f

                idx = frames.index(replace)
                frames[idx] = page

            print_step(page, frames.copy(), True)

    return faults, hits


# ---------------- Summary ---------------- #
def summary(name, faults, hits, total):

    print("\n----------------------------")
    print(name)
    print("----------------------------")
    print("Total Requests :", total)
    print("Page Faults    :", faults)
    print("Page Hits      :", hits)
    print("Hit Ratio      : {:.2f}%".format(hits / total * 100))
    print("Fault Ratio    : {:.2f}%".format(faults / total * 100))


# ---------------- Main ---------------- #
def main():

    print("=" * 45)
    print("      MEMORY PAGING SIMULATOR")
    print("=" * 45)

    frames = int(input("Enter number of frames: "))

    print("\nEnter reference string")
    print("Example: 7 0 1 2 0 3 0 4 2 3 0 3 2")

    reference = list(map(int, input("\nReference: ").split()))

    total = len(reference)

    fifo_faults, fifo_hits = fifo(reference, frames)
    lru_faults, lru_hits = lru(reference, frames)
    opt_faults, opt_hits = optimal(reference, frames)

    print("\n\n========== FINAL COMPARISON ==========")

    summary("FIFO", fifo_faults, fifo_hits, total)
    summary("LRU", lru_faults, lru_hits, total)
    summary("OPTIMAL", opt_faults, opt_hits, total)

    print("\nBest Algorithm:")

    best = min(
        [
            ("FIFO", fifo_faults),
            ("LRU", lru_faults),
            ("OPTIMAL", opt_faults),
        ],
        key=lambda x: x[1],
    )

    print(best[0], "with", best[1], "page faults")


if __name__ == "__main__":
    main()