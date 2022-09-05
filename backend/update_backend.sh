#!/bin/sh


function update_backend_container() {
    BACKEND1_SERVICE_NM=back1
    BACKEND2_SERVICE_NM=back2
    echo "Update first backend docker images and restart"

    docker compose build $BACKEND1_SERVICE_NM # 첫번째 backend service를 다시 빌드한다.
    docker compose up -d $BACKEND1_SERVICE_NM # 첫번째 backend service를 다시 시작한다.

    echo "Update second backend docker images and restart"

    docker compose build $BACKEND2_SERVICE_NM # 두번째 backend service를 다시 빌드한다.
    docker compose up -d $BACKEND2_SERVICE_NM # 두번째 backend service를 다시 시작한다.
}