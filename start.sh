docker pull jetbrains/youtrack:2026.2.16593

docker run -d -it --name youtrack \
  -v ./YouTrack/data:/opt/youtrack/data \
  -v ./YouTrack/conf:/opt/youtrack/conf \
  -v ./YouTrack/logs:/opt/youtrack/logs \
  -v ./YouTrack/backups:/opt/youtrack/backups \
  -p 8080:8080 \
  jetbrains/youtrack:2026.2.16593
