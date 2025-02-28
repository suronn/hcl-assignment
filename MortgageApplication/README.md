# Backend Assessment

### Application

This is a mortgage-application, a Java-based backend application using REST.
It contains the following endpoints.

```
- GET /api/interest-rates (get a list of current interest rates)
- POST /api/mortgage-check (post the parameters to calculate for a mortgage check)
```

The list of current mortgage rates is created in memory on application startup based on the properties configured in the
application context.
Following mortgage rates have been configured.

```
- maturityPeriod: 10, interestRate: 3.0
- maturityPeriod: 15, interestRate: 3.5
- maturityPeriod: 20, interestRate: 3.75
- maturityPeriod: 30, interestRate: 4.0
```

The mortgage rate object contains the fields:

```
- maturityPeriod (integer)
- interestRate (Percentage)
- lastUpdate (Timestamp)
```

The posted data for the mortgage check contains at least the fields;

```
- income (Amount)
- maturityPeriod (integer)
- loanValue (Amount)
- homeValue (Amount)
```

The mortgage check returns if the mortgage is feasible (boolean) and the monthly costs (Amount) of the mortgage.

Business rules applied are

```
- Mortgage should not exceed 4 times the income
- Mortgage should not exceed the home value
```
