def call() {
    sh 'docker system prune -a -f && echo "Deleted all the stale containers"'
    sh 'docker system prune -f --volumes && echo "Deleted all the stale volumes"'
}
