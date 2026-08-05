CREATE TABLE IF NOT EXISTS Session (
    id SERIAL PRIMARY KEY,
    sessionId VARCHAR(255),
    playerOne VARCHAR(255),
    playerTwo VARCHAR(255)
);