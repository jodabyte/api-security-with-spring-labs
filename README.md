# [API1:2023](https://owasp.org/API-Security/editions/2023/en/0xa1-broken-object-level-authorization/) Broken Object Level Authorization (BOLA)

APIs tend to expose endpoints that handle object identifiers, creating a wide attack surface of Object Level Access Control issues. Object level authorization checks should be considered in every function that accesses a data source using an ID from the user.

- Check the **ownership** of the object
- Use **RBAC** to restrict access

# [API2:2023](https://owasp.org/API-Security/editions/2023/en/0xa2-broken-authentication/) Broken Authentication

Authentication mechanisms are often implemented incorrectly, allowing attackers to compromise authentication tokens or to exploit implementation flaws to assume other user's identities temporarily or permanently. Compromising a system's ability to identify the client/user, compromises API security overall.

- [RFC 9700](https://www.rfc-editor.org/rfc/rfc9700.html#name-best-practices) document describes best current security practice for OAuth 2.0
- [Token profiles](https://auth0.com/docs/secure/tokens/access-tokens/access-token-profiles) define the format and claims of access tokens issued for an API. An example ist the [RFC 9068 token profile](https://www.rfc-editor.org/rfc/rfc9068.html).

## RFC 9068

- The RFC 9068 specification aims to provide a standardized and interoperable profile as an alternative. 
- Defining a common set of mandatory and optional claims and how a resource server should validate incoming JWT access tokens.
- The validation process is described in [Section 4](https://www.rfc-editor.org/rfc/rfc9068.html#name-validating-jwt-access-token). 

### Header

| Claim | Description                                                                                                                                                                                                     |
| ----- | --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `typ` | Indicates that the token is a JWT. Per the definition of "typ" in Section 4.1.9 of [RFC7515], it is RECOMMENDED that the "application/" prefix be omitted.  Therefore, the "typ" value used SHOULD be "at+jwt". |
| `alg` | The resource server **MUST** validate the signature of all incoming JWT access tokens according to [RFC7515] using the algorithm specified in the JWT "alg" Header Parameter                                    |


### Payload Structure

| Claim       | Description                                                                                                                                                                                                                                                          |
| ----------- | -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `iss`       | Issuer claim identifies the principal that issued the JWT.                                                                                                                                                                                                           |
| `exp`       | Expiration time claim identifies the expiration time on or after which the JWT **MUST NOT** be accepted for processing. The processing of the "exp" claim requires that the current date/time **MUST** be before the expiration date/time listed in the "exp" claim. |
| `aud`       | Audience claim identifies the recipients that the JWT is intended for.  Each principal intended to process the JWT **MUST** identify itself with a value in the audience claim.                                                                                      |
| `sub`       | Subject claim identifies the principal that is the subject of the JWT.                                                                                                                                                                                               |
| `client_id` | Identifies the application or client that requested the token. It is typically issued by the authorization server to the client during registration.                                                                                                                 |
| `iat`       | This claim identifies the time at which the JWT access token was issued.                                                                                                                                                                                             |
| `jti`       | Unique identifier for the access token.                                                                                                                                                                                                                              |

<br>

# [API3:2023](https://owasp.org/API-Security/editions/2023/en/0xa3-broken-object-property-level-authorization/) Broken Object Property Level Authorization

When allowing a user to access an object using an API endpoint, it is important to validate that the user has access to the specific object properties they are trying to access.

An API endpoint is vulnerable if:

- The API endpoint exposes properties of an object that are considered sensitive and should not be read by the user (previously named: "Excessive Data Exposure")
- The API endpoint allows a user to change, add/or delete the value of a sensitive object's property which the user should not be able to access (previously named: "Mass Assignment")

How To Prevent:

- **Validate** and **sanitize** input
- Keep returned data structures to the bare minimum, according to the business/functional requirements for the endpoint → use DTO to **control which properties are exposed**.
- Use RBAC to restrict access to data → for **fine-grained access controll** use Jackson JSON Views or Spring Data REST Projections 

# [API5:2023](https://owasp.org/API-Security/editions/2023/en/0xa5-broken-function-level-authorization/) Broken Function Level Authorization

Exploitation requires the attacker to send legitimate API calls to an API endpoint that they should not have access to as anonymous users or regular, non-privileged users. 

- The enforcement mechanism(s) should:
  - **deny all access by default**
  - requiring **explicit grants to specific roles** for access to every function.
- **Review your API endpoints** against function level authorization flaws, while keeping in mind the business logic of the application and groups hierarchy.

# [API6:2023](https://owasp.org/API-Security/editions/2023/en/0xa6-unrestricted-access-to-sensitive-business-flows/) Unrestricted Access to Sensitive Business Flows

An API endpoint is vulnerable if:

- When creating an API Endpoint, it is important to understand which business flow it exposes. Some business flows are more sensitive than others, in the sense that excessive access to them may harm the business.
- Common examples of sensitive business flows and risk of excessive access associated with them:

  - Purchasing a product flow - an attacker can buy all the stock of a high-demand item at once and resell for a higher price (scalping) 
  - Creating a comment/post flow - an attacker can spam the system
  - Making a reservation - an attacker can reserve all the available time slots and prevent other users from using the system

How To Prevent:

- **Business** - identify the business flows that might harm the business if they are excessively used.
- **Engineering** - choose the right protection mechanisms to mitigate the business risk:

  - **Device fingerprinting**: denying service to unexpected client devices (e.g headless browsers) tends to make threat actors use more sophisticated solutions, thus more costly for them
  - **Human detection**: using either captcha or more advanced biometric solutions (e.g. typing patterns)
  - **Non-human patterns**: analyze the user flow to detect non-human patterns (e.g. the user accessed the "add to cart" and "complete purchase" functions in less than one second)
  - **Consider** blocking IP addresses of Tor exit nodes and well-known proxies

- **Secure and limit access to APIs** that are consumed directly by machines (such as developer and B2B APIs). They tend to be an easy target for attackers because they often don't implement all the required protection mechanisms.