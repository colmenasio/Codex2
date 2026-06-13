<%@ page import="com.kor.tomcat.service.notebook.question.IQuestion" %>
<%@ page import="java.util.List" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Codex2 - Notebook</title>
    <link rel="stylesheet" href="/content/notebook/index.css">
    <link href="https://fonts.googleapis.com/css2?family=Inter:ital,wght@0,100..900;1,100..900&display=swap" rel="stylesheet">
</head>
<body>

<%
    String currentUser = (String) request.getAttribute("currentUser");
    if (currentUser == null) {
        currentUser = "Jonh Codex";
    }
    String notebookName = (String) request.getAttribute("notebookName");
    if (currentUser == null) {
        notebookName = "Missing name";
    }
    
    List<IQuestion> questions = (List<IQuestion>) request.getAttribute("questions");
    if (questions == null) {
        questions = java.util.Arrays.asList();
    }
%>

<div class="app-container">
    <!-- Upper Bar with App Name and User Info -->
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

    <!-- Main Content Area with Questions -->
    <main class="main-content">
        <div class="test-header">
            <h2><%= notebookName %></h2>
            <p>Please answer all questions carefully. Each answer will be submitted individually.</p>
            <div class="progress-indicator">
                <span id="answeredCount">0</span> of <%= questions.size() %> questions answered
            </div>
        </div>

        <form id="testForm" method="post" action="submitTest">
            <div class="questions-container">
                <%
                    int index = 0;
                    for (IQuestion q : questions) {
                        String questionId = "q" + index;
                        String defaultAnswer = (q.getDefaultAnswer() != null) ? q.getDefaultAnswer() : "";
                %>
                <div class="question-card" data-question-id="<%= questionId %>">
                    <div class="question-header">
                        <div class="question-number">Question <%= index + 1 %></div>
                        <div class="answer-status" id="status-<%= questionId %>">Not answered</div>
                    </div>
                    
                    <div class="question-title">
                        <h3><%= escapeHtml(q.getTitle()) %></h3>
                    </div>
                    
                    <div class="question-body">
                        <p class="question-text"><%= escapeHtml(q.getQuestion()) %></p>
                        
                        <div class="answer-section">
                            <label for="<%= questionId %>" class="answer-label">
                                Your Answer:
                            </label>
                            <textarea 
                                id="<%= questionId %>" 
                                name="<%= questionId %>" 
                                class="answer-textbox"
                                placeholder="Type your answer here..."
                                rows="6"
                                oninput="updateAnswerStatus('<%= questionId %>')"
                            ><%= escapeHtml(defaultAnswer) %></textarea>
                            
                            <div class="char-counter">
                                <span id="charCount-<%= questionId %>">0</span> characters
                            </div>
                        </div>
                    </div>
                    
                    <div class="question-actions">
                        <button type="button" class="submit-question-btn" onclick="submitQuestion('<%= questionId %>')">
                            Submit Answer
                        </button>
                        <button type="button" class="clear-btn" onclick="clearAnswer('<%= questionId %>')">
                            Clear
                        </button>
                    </div>
                    
                    <div class="submission-feedback" id="feedback-<%= questionId %>"></div>
                </div>
                <%
                        index++;
                    }
                %>
            </div>
            
            <!-- Hidden field to store answers -->
            <input type="hidden" name="allAnswers" id="allAnswers">
        </form>
    </main>

    <footer class="app-footer">
        <p>&copy; 2025 Codex2 - Empowering Knowledge Assessment</p>
    </footer>
</div>

<script>
    const totalQuestions = <%= questions.size() %>;
</script>
<script type="text/javascript" src="/content/notebook/index.js"></script>

</body>
</html>

<%!
    // Helper method to escape HTML special characters
    private String escapeHtml(String text) {
        if (text == null) return "";
        return text.replace("&", "&amp;")
                   .replace("<", "&lt;")
                   .replace(">", "&gt;")
                   .replace("\"", "&quot;")
                   .replace("'", "&#39;");
    }
%>