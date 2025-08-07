📌 Stateful Authentication – Summary
🔐 1. Login
Client sends username & password.
Server validates credentials.
Server creates a session ID and stores user info in server-side session store (memory, Redis, or DB).
Server sends session ID to a client via a cookie.

🔁 2. Authenticated Requests
Client automatically sends session ID in Cookie with each request. (by default stores in browser cookie)
Server looks up the session ID in its session store.
If session exists → user is authenticated ✅,
If session is missing/expired → user is unauthenticated ❌

🚪 3. Logout
Client sends logout request.
Server deletes the session from session store.
Session ID becomes invalid → future requests are unauthorized.
(Optional) Server sends back a Set-Cookie header to remove the cookie from the client.

🧠 Example:
Login → Set-Cookie: sessionId=abc123
Request → Cookie: sessionId=abc123
Server → Looks up sessionId in memory and authenticates user
Logout → Server removes sessionId from memory
⚠️ Cons of Stateful Authentication
❌ Limitation	📄 Explanation
Not scalable	Session data must be stored server-side. Harder to scale across multiple instances without shared session store (e.g., Redis).
Session Hijacking Risk	If session ID is stolen (e.g., via XSS), attacker can impersonate the user.
CSRF Vulnerable	Since cookies are automatically sent, it’s vulnerable to CSRF attacks unless CSRF tokens or SameSite cookies are used.
Server memory overhead	Server must track all active sessions, which uses resources.
Logout required to invalidate session	Unlike JWTs that expire automatically, sessions need to be explicitly destroyed to end user access.