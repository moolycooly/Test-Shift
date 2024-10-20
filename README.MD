# API:

## Seller API

### Список всех продавцов

- **URL:** `/seller`
- **Метод:** `GET`
- **Описание:** Возвращает всех продавцов
- **Response:**
    - Status: `200 OK`

- ***Пример:*** `/seller`
  - response:
  ```json
  [
    {
        "id": 1,
        "name": "Alberto Mayert",
        "contactInfo": "878-999-0161",
        "registrationDate": "2022-10-22T14:30:00"
    },
    {
        "id": 2,
        "name": "Elmer Runte",
        "contactInfo": "645-423-7550",
        "registrationDate": "2024-09-03T09:45:00"
    }
  ]
  ```
### Информация о конкретном продавце

- **URL:** `/seller/{id}`
- **Параметры запроса**
  - (необязательно) `transactions` тип `boolean`, если активен, то возвращает список транзакций продавца вместе с его информацией
- **Метод:** `GET`
- **Описание:** Возвращает информацию о продавце по его id. Если передать в параметрах transactions=true, то вернет еще и список его транзакций. Если такого продавца нет, то вернет 404.
- **Responses:**
    - Status: `200 OK`
    - Status: `404 NOT FOUND`

- ***Пример:*** `/seller/12?transactions=true`
   - response:
  ```json
  {
    "id": 9,
    "name": "Betty Padberg",
    "contactInfo": "802-395-7310",
    "registrationDate": "2024-10-20T15:52:09.227133",
    "transactions": [
        {
            "id": 13,
            "amount": 156.07,
            "paymentType": "TRANSFER",
            "transactionDate": "2024-10-20T17:09:55.822576",
            "sellerId": 9
        },
        {
            "id": 14,
            "amount": 919.07,
            "paymentType": "TRANSFER",
            "transactionDate": "2024-10-20T17:09:56.685443",
            "sellerId": 9
        }
    ]
  }
  ```
### Создать нового продавца

- **URL:** `/seller`
- **Тело запроса** 
    - `name` тип `String`
    - (необязательно) `contactInfo` тип `String`
- **Метод:** `POST`
- **Responses:**
    - Status: `201 CREATED`

- ***Пример:***
    - URL: `/seller`
    - request:
      ```json
      {   
         "name": "Darin Gottlieb",
         "contactInfo": "885-796-3985"
      }
      ```
    - response:
      ```json
      {
        "id": 11,
        "name": "Darin Gottlieb",
        "contactInfo": "885-796-3985",
        "registrationDate": "2024-10-20T17:16:48.9827674"
      }
      ```
### Обновить продавца

- **URL:** `/seller/{id}`
- **Тело запроса**
    - (необязательно) `name` тип `String`
    - (необязательно) `contactInfo` тип `String`
- **Метод:** `PUT`
- **Описание:** Обновляет информацию о продавца. Если какое-то поле не пустое, то продавцу присвоят это поле. 

- **Responses:**
    - Status: `200 OK`
    - Status: `404 NOT FOUND`

- ***Пример:*** `/seller/1`
    - request:
```json 
{
   "contactInfo": "885-796-3985"
}
```
### Удалить продавца продавца

- **URL:** `/seller/{id}`
- **Метод:** `DELETE`
- **Описание:** Удаляет продавца по переданному id. Если такого продавца нет то в ответе код 404.

- **Responses:**
    - Status: `204 NO CONTENT`
    - Status: `404 NOT FOUND`
### Получить самого продуктивного продавца

- **URL:** `/seller/most-productive`
- **Параметры запроса**
    - `period`:
        - `DAY,MONTH,QUARTER,YEAR`
- **Метод:** `GET`
- **Описание:** Возвращает самого продуктивного продавца,тот у которого сумма всех транзакции больше всех в рамках выбранного периода.
- **Responses:**
    - Status: `200 OK`
    - Status: `404 NOT FOUND`

- ***Пример:*** `/seller/most-productive?period=year`
    - response:
  ```json
   {
    "id": 9,
    "name": "Betty Padberg",
    "contactInfo": "802-395-7310",
    "registrationDate": "2024-10-20T15:52:09.227133"
   }
  ```
### Получить список продавцов с суммой меньше указанной

- **URL:** `seller/less-then-summa`
- **Параметры запроса**
    - `dateFrom`:
        - `yyyy-mm-dd`
    - `dateTo`:
        - `yyyy-mm-dd`
    - `summa`:
        - `double`
        - пример : 100.5
- **Метод:** `GET`
- **Описание:** Возвращает список продавцов, у которых сумма всех транзакции за выбранный период
  меньше переданного параметра суммы
- **Responses:**
    - Status: `200 OK`

- ***Пример:*** `/seller/less-then-summa?dateFrom=2024-10-24&dateTo=2025-10-23&summa=87.12`
    - response:
  ```json
  [
    {
        "id": 2,
        "name": "Elmer Runte",
        "contactInfo": "645-423-7550",
        "registrationDate": "2024-09-03T09:45:00"
    },
    {
        "id": 3,
        "name": "Christina Zieme",
        "contactInfo": "921-270-2943",
        "registrationDate": "2024-10-19T12:00:00"
    }
  ]
  ```
## Transaction API
### Список всех транзакций

- **URL:** `/transaction`
- **Метод:** `GET`
- **Response:**
    - Status: `200 OK`
- ***Пример:*** `/transaction`
    - response:
  ```json
  [
    {
        "id": 4,
        "amount": 12.53,
        "paymentType": "CARD",
        "transactionDate": "2024-09-03T10:00:00",
        "sellerId": 2
    },
    {
        "id": 5,
        "amount": 52.78,
        "paymentType": "CASH",
        "transactionDate": "2024-09-03T11:00:00",
        "sellerId": 2
    }
  ]
  ```
### Информация о конкретной транзакции

- **URL:** `/transaction/{id}`
- **Метод:** `GET`
- **Responses:**
    - Status: `200 OK`
    - Status: `404 NOT FOUND`
- ***Пример:*** `/transaction/3`
    - response:
  ```json
   {
    "id": 3,
    "amount": 325.51,
    "paymentType": "TRANSFER",
    "transactionDate": "2024-10-16T14:45:00",
    "sellerId": 1
    }
  ```
### Создать новую транзакцию

- **URL:** `/transaction`
- **Тело запроса**
    - `sellerId` тип `int`
    - `amount` тип `int`
    - `paymentType` тип `string`
- **Метод:** `POST`
- **Responses:**
    - Status: `201 CREATED`
- ***Пример:*** `/transaction`
    - Request:
      ```json
      {   
           "sellerId" : 9,
           "amount": 889.46,
           "paymentType": "CASH"
      }
      ```
    - Response:
      ```json
      {
           "id": 19,
           "amount": 889.46,
           "paymentType": "CASH",
           "transactionDate": "2024-10-20T17:43:22.4469899",
           "sellerId": 9
      }
      ```




  
  


