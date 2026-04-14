db = connect("mongodb://localhost:27017/chat_db");
db.createUser({
    user: "chat_service",
    pwd: "dummy_password", // password must be specified
    roles: [
        {
            role: "readWrite",
            db: "chat_db"
        }
    ]
});