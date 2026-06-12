<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
    <!DOCTYPE html>
    <html lang="en">

    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Codex2 — Sign in</title>
        <link rel="stylesheet" href="/content/login/signup/index.css">
        <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700;800&display=swap"
            rel="stylesheet">
    </head>

    <body>
        <main class="login-shell">
            <aside class="login-panel" aria-label="Account Creation">
                <div class="panel-card panel-sign-up">
                    <p class="eyebrow">Existing Account </p>
                    <h2>You already have an account?</h2>
                    <div>
                        <span class="sign-up-text">You should sign in</span>
                        <a class="sign-up-text-link" href="/login/signin">NOW!!!</a>
                    </div>
                </div>
                <div class="panel-card panel-mini">
                    <span class="badge">Today's joke:</span>
                    <p>This entire subject
                    </p>
                </div>
            </aside>

            <section class="login-card">
                <header class="login-header">
                    <div class="brand-row">
                        <div class="brand-icon">C²</div>
                        <div>
                            <p class="eyebrow">Welcome twin</p>
                            <h1>Codex2</h1>
                        </div>
                    </div>
                    <p class="lead">Sign up with a username and password to continue.</p>
                </header>

                <form class="login-form" id="loginForm", onsubmit="return false">
                    <label class="field-label" for="username">Username</label>
                    <input id="username" name="username" type="text" placeholder="Enter your username"
                        autocomplete="username" required>

                    <label class="field-label" for="password">Password</label>
                    <input id="password" name="password" type="password" placeholder="Enter your password"
                        autocomplete="current-password" required>

                    <div class="error-message"></div>

                    <button class="primary-btn" type="submit">Sign up</button>
                </form>

                <footer class="login-footer">
                    <p>TODO!!!</p>
                    <a class="text-link" href="/content/notebook/notebook.jsp">TODO</a>
                </footer>
            </section>

        </main>
    </body>

    <script type="text/javascript" src="/content/login/signup/index.js"></script>

    </html>