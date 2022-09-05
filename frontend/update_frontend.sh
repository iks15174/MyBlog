#!/bin/sh


function update_front_container() {
    FRONT_SERVICE_NM=front
    echo "Update front container"

    docker cp ./frontend/dist $FRONT_SERVICE_NM:/usr/share/nginx/html # 새로운 빌드 파일로 교체
    docker exec -it $FRONT_SERVICE_NM nginx -s reload # nginx를 다시 reload한다.
}