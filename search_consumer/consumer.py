from kafka import KafkaConsumer
import json
import time


consumer = KafkaConsumer(
    "post",
    bootstrap_servers=[],
    auto_offset_reset="earliest",
    enable_auto_commit=True,
    group_id="post_consumer",
    value_deserializer=lambda x: json.loads(x.decode("utf-8")),
    consumer_timeout_ms=1000
)

while True:
    try:
        message = consumer.poll()
        if not message:
            time.sleep(1)
             
        if message.error():
            print("Cosumer error " + message.error())
            continue
        
        # search 로 메세지 보낼것
                    
    except:
        print("Kafka exception occured")
        
    finally:
        consumer.close()
        break


