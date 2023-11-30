FROM python:3.12
RUN pip install paho-mqtt tqdm

COPY sender.py /opt/

ENTRYPOINT [ "python", "/opt/sender.py" ]
CMD [ "--help" ]
