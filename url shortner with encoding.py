import hashlib

url_database = {}

def shorten_url(long_url):
    short_code = hashlib.md5(long_url.encode()).hexdigest()[:6]
    url_database[short_code] = long_url
    return f"http://short.ly/{short_code}"

def retrieve_url(short_url):
    short_code = short_url.split("/")[-1]
    return url_database.get(short_code, "URL not found")

while True:
    print("\n1. Shorten URL")
    print("2. Retrieve URL")
    print("3. Exit")

    choice = input("Enter choice: ")

    if choice == "1":
        long_url = input("Enter long URL: ")
        short_url = shorten_url(long_url)
        print("Short URL:", short_url)

    elif choice == "2":
        short_url = input("Enter short URL: ")
        print("Original URL:", retrieve_url(short_url))

    elif choice == "3":
        print("Goodbye!")
        break

    else:
        print("Invalid choice.")