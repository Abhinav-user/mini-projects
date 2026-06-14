import hashlib
import json
import os

DATABASE_FILE = "urls.json"

# Load database
if os.path.exists(DATABASE_FILE):
    with open(DATABASE_FILE, "r") as file:
        url_database = json.load(file)
else:
    url_database = {}


def save_database():
    with open(DATABASE_FILE, "w") as file:
        json.dump(url_database, file, indent=4)


def shorten_url(long_url, custom_alias=None):
    # Check for duplicates
    for code, data in url_database.items():
        if data["url"] == long_url:
            return f"http://short.ly/{code}"

    if custom_alias:
        if custom_alias in url_database:
            return "Alias already exists."
        short_code = custom_alias
    else:
        short_code = hashlib.md5(long_url.encode()).hexdigest()[:6]

    url_database[short_code] = {
        "url": long_url,
        "clicks": 0
    }

    save_database()

    return f"http://short.ly/{short_code}"


def retrieve_url(short_url):
    short_code = short_url.split("/")[-1]

    if short_code in url_database:
        url_database[short_code]["clicks"] += 1
        save_database()
        return url_database[short_code]["url"]

    return "URL not found."


def delete_url(short_code):
    if short_code in url_database:
        del url_database[short_code]
        save_database()
        return "URL deleted successfully."

    return "URL not found."


def search_url(keyword):
    found = False

    for code, data in url_database.items():
        if keyword.lower() in data["url"].lower() or keyword.lower() in code.lower():
            print(f"{code} -> {data['url']} (Clicks: {data['clicks']})")
            found = True

    if not found:
        print("No matching URLs found.")


def analytics():
    total_urls = len(url_database)

    total_clicks = sum(
        data["clicks"]
        for data in url_database.values()
    )

    print("\n===== ANALYTICS =====")
    print("Total URLs:", total_urls)
    print("Total Clicks:", total_clicks)

    if total_urls > 0:
        most_visited = max(
            url_database.items(),
            key=lambda item: item[1]["clicks"]
        )

        print(
            f"Most Visited: {most_visited[0]} "
            f"({most_visited[1]['clicks']} clicks)"
        )


while True:

    print("\n====== URL SHORTENER ======")
    print("1. Shorten URL")
    print("2. Retrieve URL")
    print("3. Search URL")
    print("4. Delete URL")
    print("5. Analytics")
    print("6. View All URLs")
    print("7. Exit")

    choice = input("\nEnter choice: ")

    if choice == "1":

        long_url = input("Enter long URL: ")

        custom = input(
            "Custom alias? (leave blank for auto): "
        )

        if custom == "":
            custom = None

        short_url = shorten_url(long_url, custom)

        print("Short URL:", short_url)

    elif choice == "2":

        short_url = input("Enter short URL: ")

        print("Original URL:", retrieve_url(short_url))

    elif choice == "3":

        keyword = input("Search keyword: ")

        search_url(keyword)

    elif choice == "4":

        code = input("Enter shortcode to delete: ")

        print(delete_url(code))

    elif choice == "5":

        analytics()

    elif choice == "6":

        if not url_database:
            print("No URLs stored.")

        else:
            print("\nStored URLs:")

            for code, data in url_database.items():
                print(
                    f"{code} -> {data['url']} "
                    f"(Clicks: {data['clicks']})"
                )

    elif choice == "7":

        print("Goodbye!")
        break

    else:

        print("Invalid choice.")