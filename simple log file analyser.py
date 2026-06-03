error_count = 0
warning_count = 0
info_count = 0

with open("log.txt", "r") as file:
    for line in file:
        if "ERROR" in line:
            error_count += 1
        elif "WARNING" in line:
            warning_count += 1
        elif "INFO" in line:
            info_count += 1

print("Log Summary")
print("-----------")
print("INFO:", info_count)
print("WARNING:", warning_count)
print("ERROR:", error_count)