from dotenv import load_dotenv
load_dotenv()
from embedchain import App
from time import sleep
from fastapi import FastAPI
from schema import UserQuery, UserQueryResponse


amy = App.from_config(yaml_path="config.yaml")

for i in range(1,11):
    amy.add(f'./amy_data/model_datasets_{i}.txt', data_type='text')
    print(f"{i*100} rows added. Sleeping for 20 seconds to avoid rate limiting.")
    sleep(21)

# while 1:
#     query_text = input("Enter your query: ")
#     response = amy.query(f"""INSTRUCTION: Given the product description, print ONLY the similar product_id
#                 DESCRIPTION:{query_text}
#                 PRODUCT_IDs: ?""")
#     print()
#     print(response)
app = FastAPI()

# create a query route which accept a query string and returns a response
@app.post("/query")
def query(query_text: UserQuery)->UserQueryResponse:
    """Accepts a query string and returns a response"""
    response = amy.query(f"""INSTRUCTION: Given the product description, print ONLY the similar product_id
                DESCRIPTION:{query_text.query}
                PRODUCT_IDs: ?""")
    return UserQueryResponse(response=response)