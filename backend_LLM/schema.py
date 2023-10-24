from pydantic import BaseModel

class UserQuery(BaseModel):
    """User Query Schema"""
    query: str
    device: str = "Android/iOS"
    language: str = "English"


class UserQueryResponse(BaseModel):
    """User Query Response Schema"""
    status: bool = True
    response: str