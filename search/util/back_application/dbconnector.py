import mysql.connector
from search.settings import BACK_APPL_DATABASE


class DBConnector:

    db_name = "default"
    database = BACK_APPL_DATABASE[db_name]["NAME"] 
    user = BACK_APPL_DATABASE[db_name]["USER"]
    password = BACK_APPL_DATABASE[db_name]["PASSWORD"]
    host = BACK_APPL_DATABASE[db_name]["HOST"]
    port = BACK_APPL_DATABASE[db_name]["PORT"]

    def connect(self):
        try:
            mysql_con = mysql.connector.connect(
                host=self.host,
                port=self.port,
                database=self.database,
                user=self.user,
                password=self.password,
            )
            self.mysql_cursor = mysql_con.cursor(dictionary=True)
        except Exception as e:
            print(e.message)
            
    def get_post_by_id(self, post_id):
        sql = "select * from Posts where id = %d ;"
        self.mysql_cursor.execute(sql, post_id)
        return self.mysql_cursor.fetchone()
    
    def __delete__(self):
        if self.mysql_cursor:
            self.mysql_cursor.close()
