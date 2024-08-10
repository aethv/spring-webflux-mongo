# spring-webflux-mongo

### Steps
1. Start mongoDB
```bash
docker run -d --name mongodb -p 27017:27017 mongo:7.0.6
```

2. Run the application
```bash
./mvnw clean spring-boot:run
````

3. Test the application

<ul>
<li>Get All Books </li>

```bash
curl -i localhost:8080/api/books
```

<li>Create Books </li>

```bash
curl -i -X POST localhost:8080/api/books \
  -H 'Content-Type: application/json' \
  -d '{"title": "Spring in Action", "author": "Craig Walls and Ryan Breidenbach", "year": 1005}'
```

<li>Update Books </li>

```bash
curl -i -X PATCH localhost:8080/api/books/$BOOK_ID \
  -H 'Content-Type: application/json' \
  -d '{"year": 2005}'
```

<li>Get Book By id</li>

```bash
curl -i localhost:8080/api/books/$BOOK_ID
```

<li>Delete Book</li>

```bash
curl -i -X DELETE localhost:8080/api/books/$BOOK_ID
```

</ul>


