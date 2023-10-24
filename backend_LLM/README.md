# AMY Backend
Backend service for LLM AMY

# Usage

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
