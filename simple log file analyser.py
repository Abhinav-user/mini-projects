error_count = 0
warning_count = 0
info_count = 0

errors = []
word_counts = {}
longest_line = ""
total_lines = 0

filename = input("Enter log file name: ")

try:
    with open(filename, "r", encoding="utf-8") as file:

        for line_num, line in enumerate(file, start=1):
            total_lines += 1

            line = line.strip()

            if len(line) > len(longest_line):
                longest_line = line

            if "ERROR" in line:
                error_count += 1
                errors.append(f"Line {line_num}: {line}")

            if "WARNING" in line:
                warning_count += 1

            if "INFO" in line:
                info_count += 1

            words = line.split()

            for word in words:
                word = word.upper()
                word_counts[word] = word_counts.get(word, 0) + 1

    print("\n" + "=" * 40)
    print("LOG FILE ANALYSIS REPORT")
    print("=" * 40)

    print(f"Total Lines   : {total_lines}")
    print(f"INFO Entries  : {info_count}")
    print(f"WARNING Entries: {warning_count}")
    print(f"ERROR Entries : {error_count}")

    print("\nLongest Log Entry:")
    print(longest_line)

    print("\nError Details:")
    if errors:
        for error in errors:
            print(error)
    else:
        print("No errors found.")

    print("\nTop 10 Most Common Words:")
    sorted_words = sorted(
        word_counts.items(),
        key=lambda item: item[1],
        reverse=True
    )

    for word, count in sorted_words[:10]:
        print(f"{word}: {count}")

    keyword = input("\nEnter a keyword to search (or press Enter to skip): ")

    if keyword:
        print(f"\nMatches for '{keyword}':")

        with open(filename, "r", encoding="utf-8") as file:
            found = False

            for line_num, line in enumerate(file, start=1):
                if keyword.lower() in line.lower():
                    print(f"Line {line_num}: {line.strip()}")
                    found = True

            if not found:
                print("No matches found.")

    with open("analysis_report.txt", "w", encoding="utf-8") as report:
        report.write("LOG FILE ANALYSIS REPORT\n")
        report.write("=" * 40 + "\n")
        report.write(f"Total Lines: {total_lines}\n")
        report.write(f"INFO: {info_count}\n")
        report.write(f"WARNING: {warning_count}\n")
        report.write(f"ERROR: {error_count}\n\n")

        report.write("Error Details:\n")
        for error in errors:
            report.write(error + "\n")

    print("\nReport saved as 'analysis_report.txt'")

except FileNotFoundError:
    print("File not found.")
except Exception as e:
    print("An error occurred:", e)