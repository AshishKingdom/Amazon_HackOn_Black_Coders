# AMY Backend
Backend service for LLM AMY

# API Usage

### To get product_id of based on user query

```http

POST /query

```

### Request Body
```json
{
  "query": "mero ko ek 5g phone chaiye jisme AMOLED display ho",
  "device": "Android/iOS",
  "language": "English"
}
```

### Response

```json
{
  "status": true,
  "response": "PRODUCT_IDs: 1289"
}
```

# Running the project

## With Docker

1. Build the docker image with the following command
   ```bash
   sudo docker build -t amazon_hackon_amy_backend .
   ```
2. Run the docker image
   ```bash
   sudo docker run -dit --rm -p 4321:4321 amazon_hackon_amy_backend
   ```
3. The service will be live on `localhost:4321`

## Without Docker
1. Setup the virtual environment
   ```bash
   python -m venv .venv
   ```
2. Activate the virtual environment
   ```bash
   source /.venv/bin/activate
   ```
3. Install all the dependencies
   ```bash
   pip install -r requirements.txt
   ```
4. Start the application
   ```bash
   uvicorn main:app --host 0.0.0.0 --port 4321
   ```
5. The application will be live on `localhost:4321`
6. If you encounter any error, try using Python 3.10.0

## Notes
1. The response of the model is strictly based on the training dataset (only 2000 products)
2. The LLM model can cause hallucinations sometimes

