from sqlalchemy import *


db = create_engine('sqlite:///cache.db')
db.echo = True

metadata = MetaData(db)

cache = Table('cache', metadata,
              Column('id', Integer, primary_key=True),
              Column('source', String(16)),
              Column('location', String(16)),
              Column('datetime', Integer),
              Column('json', String)
)
cache.create()
