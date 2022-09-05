FROM python:3.7
WORKDIR /app
COPY . ./
COPY ../requirements.txt ./
RUN pip install -r requirements.txt
ENTRYPOINT ["python", "consumer.py"]