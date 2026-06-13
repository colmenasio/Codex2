<%@ page import="com.kor.tomcat.service.notebook.NotebookListing" %>
<%@ page import="java.util.List" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Codex2 - Home</title>
    <link href="https://fonts.googleapis.com/css2?family=Inter:ital,wght@0,100..900;1,100..900&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="/content/home/index.css">
</head>
<body>

<%
    String currentUser = (String) request.getAttribute("currentUser");
    if (currentUser == null) {
        currentUser = "Jonh Codex";
    }
    
    List<NotebookListing> notebooks = (List<NotebookListing>) request.getAttribute("notebooks");
    if (notebooks== null) {
        notebooks = java.util.Arrays.asList();
    }
%>

<div class="app-container">
    <!-- Upper Bar with App Name and User Info (identical style) -->
    <header class="app-header">
        <div class="logo-section">
            <div class="app-icon">C²</div>
            <h1 class="app-name">Codex2</h1>
            <span class="app-tagline">if codex is so good why dont they make codex 2 lmao</span>
        </div>
        <div class="user-section">
            <div class="user-avatar">
                <%= currentUser.substring(0, 1).toUpperCase() %>
            </div>
            <div class="user-info">
                <span class="user-greeting">Welcome,</span>
                <span class="user-name"><%= currentUser %></span>
            </div>
            <button class="logout-btn" onclick="handleLogout()">Logout</button>
        </div>
    </header>

    <!-- Main Content Area: Home Menu -->
    <main class="main-content">
        <!-- Welcome Section: "Welcome home, USERNAME" -->
        <div class="welcome-header">
            <h1>🏠 Home Dashboard</h1>
            <div class="welcome-greeting">
                Welcome home, <strong><%= currentUser %></strong>
            </div>
        </div>

        <!-- Notebooks Panel with whiteish background and list of hrefs + display names -->
        <div class="notebooks-panel">
            <div class="panel-header">
                <h2>Notebooks</h2>
                <div class="panel-subtitle">Your notebooks bestie :P</div>
            </div>
            
            <%
                if (notebooks == null || notebooks.isEmpty()) {
            %>
                <div class="empty-notebooks">
                    <p>No notebooks available at the moment. Check back later</p>
                </div>
            <%
                } else {
            %>
                <ul class="notebooks-list">
                    <%
                        for (NotebookListing listing : notebooks) {
                            String display = listing.name;
                            String href = listing.url;
                            // Fallback for safety
                            if (display == null) display = "Untitled Notebook";
                            if (href == null) href = "#";
                    %>
                        <li class="notebook-item">
                            <a href="<%= href %>" class="notebook-link">
                                <div class="link-icon">📖</div>
                                <div class="link-info">
                                    <div class="link-display-name"><%= display %></div>
                                    <div class="link-url"><%= href %></div>
                                </div>
                                <div class="arrow-indicator">→</div>
                            </a>
                        </li>
                    <%
                        }
                    %>
                </ul>
            <%
                }
            %>
        </div>
    </main>

    <footer class="app-footer">
        <p>&copy; 2025 Codex2 - Empowering Knowledge Assessment</p>
    </footer>
</div>

<script>
// Handle logout
async function handleLogout() {
    if (confirm('Are you sure you want to logout?')) {
        const payload = {
            action: "logout",
            authToken: localStorage.getItem("authToken"),
        };

        try {
            const response = await fetch('/api/login', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Accept': 'application/json'
                },
                body: JSON.stringify(payload)
            });
            window.location.href = '/login/signin';
        } catch (e) {

        }
    }
}
</script>

</body>
</html>