source ./backend/update_backend.sh
source ./frontend/update_frontend.sh

echo "Move to backend directory"
cd backend

echo "Build backend file"
./gradlew clean build -x test
cd ..

echo "Move to frontend directory"
cd frontend

echo "Build frontend file"
npm run build
cd ..

# 설명 추가할 것

echo "Check project aleary deployed"
FRONT_CONTAINER_NM=front
ALEADY_DELPOYED=$(docker ps --filter "name=$FRONT_CONTAINER_NM" | wc -l)
if [ ${ALEADY_DELPOYED} -gt 1 ];
then
    echo "Project aleardy deployed before"
    update_backend_container
    update_front_container
    

else
    echo "Project not deployed"
    docker compose up -d
fi