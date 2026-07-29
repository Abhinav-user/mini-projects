movies = {
    "Inception": {"Genre": "Sci-Fi", "Rating": 8.8},
    "Interstellar": {"Genre": "Sci-Fi", "Rating": 8.6},
    "The Matrix": {"Genre": "Sci-Fi", "Rating": 8.7},
    "The Dark Knight": {"Genre": "Action", "Rating": 9.0},
    "Avengers: Endgame": {"Genre": "Action", "Rating": 8.4},
    "John Wick": {"Genre": "Action", "Rating": 7.4},
    "Titanic": {"Genre": "Romance", "Rating": 7.9},
    "The Notebook": {"Genre": "Romance", "Rating": 7.8},
    "La La Land": {"Genre": "Romance", "Rating": 8.0}
}


def find_movie(name):
    for movie in movies:
        if movie.lower() == name.lower():
            return movie
    return None


while True:
    print("\n========== Movie Recommendation System ==========")
    print("1. Search Movie")
    print("2. Browse by Genre")
    print("3. Show Top Rated Movie")
    print("4. Add New Movie")
    print("5. Show All Movies")
    print("6. Exit")

    choice = input("\nEnter your choice: ")

    if choice == "1":
        name = input("Enter movie name: ")
        movie = find_movie(name)

        if movie:
            genre = movies[movie]["Genre"]
            rating = movies[movie]["Rating"]

            print("\nMovie Details")
            print("-" * 30)
            print("Title :", movie)
            print("Genre :", genre)
            print("Rating:", rating)

            print("\nRecommended Movies")
            print("-" * 30)

            found = False
            for title, details in movies.items():
                if details["Genre"] == genre and title != movie:
                    print(f"{title}  | Rating: {details['Rating']}")
                    found = True

            if not found:
                print("No recommendations available.")

        else:
            print("Movie not found!")

    elif choice == "2":
        genre = input("Enter genre: ").title()

        try:
            min_rating = float(input("Minimum rating (0-10): "))
        except ValueError:
            print("Invalid rating!")
            continue

        found = False
        print("\nMatching Movies")
        print("-" * 30)

        for title, details in movies.items():
            if details["Genre"] == genre and details["Rating"] >= min_rating:
                print(f"{title} ({details['Rating']})")
                found = True

        if not found:
            print("No movies found.")

    elif choice == "3":
        best_movie = max(movies, key=lambda x: movies[x]["Rating"])

        print("\nTop Rated Movie")
        print("-" * 30)
        print("Title :", best_movie)
        print("Genre :", movies[best_movie]["Genre"])
        print("Rating:", movies[best_movie]["Rating"])

    elif choice == "4":
        title = input("Movie Title: ").strip()

        if find_movie(title):
            print("Movie already exists!")
            continue

        genre = input("Genre: ").title()

        try:
            rating = float(input("Rating (0-10): "))

            if rating < 0 or rating > 10:
                print("Rating must be between 0 and 10.")
                continue

        except ValueError:
            print("Invalid rating!")
            continue

        movies[title] = {
            "Genre": genre,
            "Rating": rating
        }

        print("Movie added successfully!")

    elif choice == "5":
        print("\nAll Movies")
        print("-" * 45)

        sorted_movies = sorted(
            movies.items(),
            key=lambda x: x[1]["Rating"],
            reverse=True
        )

        for title, details in sorted_movies:
            print(f"{title:22} {details['Genre']:10} {details['Rating']}")

    elif choice == "6":
        print("Thank you for using the Movie Recommendation System!")
        break

    else:
        print("Invalid choice. Please try again.")