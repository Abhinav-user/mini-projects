# Movie Recommendation System

movies = {
    "Inception": {
        "Genre": "Sci-Fi",
        "Rating": 8.8
    },
    "Interstellar": {
        "Genre": "Sci-Fi",
        "Rating": 8.6
    },
    "The Matrix": {
        "Genre": "Sci-Fi",
        "Rating": 8.7
    },
    "The Dark Knight": {
        "Genre": "Action",
        "Rating": 9.0
    },
    "Avengers: Endgame": {
        "Genre": "Action",
        "Rating": 8.4
    },
    "John Wick": {
        "Genre": "Action",
        "Rating": 7.4
    },
    "Titanic": {
        "Genre": "Romance",
        "Rating": 7.9
    },
    "The Notebook": {
        "Genre": "Romance",
        "Rating": 7.8
    },
    "La La Land": {
        "Genre": "Romance",
        "Rating": 8.0
    }
}

print("===== Movie Recommendation System =====")

while True:

    movie = input("\nEnter a movie name (or type Exit): ")

    if movie.lower() == "exit":
        print("Thank you for using the Movie Recommendation System!")
        break

    if movie not in movies:
        print("Movie not found!")
        continue

    genre = movies[movie]["Genre"]

    print("\nMovie Details")
    print("--------------------")
    print("Title :", movie)
    print("Genre :", genre)
    print("Rating:", movies[movie]["Rating"])

    print("\nRecommended Movies")
    print("--------------------")

    found = False

    for title, details in movies.items():
        if details["Genre"] == genre and title != movie:
            print(f"{title} (Rating: {details['Rating']})")
            found = True

    if not found:
        print("No recommendations available.")