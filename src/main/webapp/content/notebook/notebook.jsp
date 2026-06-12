<%@ page import="com.kor.tomcat.service.notebook.Question" %>
<%@ page import="java.util.List" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Codex2 - Cause if codex is so good then why dont the make codex 2 huh??</title>
    <link rel="stylesheet" href="/content/notebook/notebook.css">
    <link href="https://fonts.googleapis.com/css2?family=Inter:ital,wght@0,100..900;1,100..900&display=swap" rel="stylesheet">
</head>
<body>

<%
    // Lmao todo fix this shite
    String currentUser = "John Codex";
    
    List<Question> questions = (List<Question>) request.getAttribute("questions");
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
            <span class="app-tagline">"if codex is so good why dont they make codex 2 lmao"</span>
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
            <h2>Assessment Test</h2>
            <p>Please answer all questions carefully. Each answer will be submitted individually.</p>
            <div class="progress-indicator">
                <span id="answeredCount">0</span> of <%= questions.size() %> questions answered
            </div>
        </div>

        <form id="testForm" method="post" action="submitTest">
            <div class="questions-container">
                <%
                    int index = 0;
                    for (Question q : questions) {
                        String questionId = "q" + index;
                        String defaultAnswer = (q.default_answer != null) ? q.default_answer : "";
                %>
                <div class="question-card" data-question-id="<%= questionId %>">
                    <div class="question-header">
                        <div class="question-number">Question <%= index + 1 %></div>
                        <div class="answer-status" id="status-<%= questionId %>">Not answered</div>
                    </div>
                    
                    <div class="question-title">
                        <h3><%= escapeHtml(q.title) %></h3>
                    </div>
                    
                    <div class="question-body">
                        <p class="question-text"><%= escapeHtml(q.question) %></p>
                        
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
    // Track answered questions
    let answeredQuestions = new Set();
    
    // Update character count and answer status
    function updateAnswerStatus(questionId) {
        const textarea = document.getElementById(questionId);
        const charCountSpan = document.getElementById('charCount-' + questionId);
        const statusSpan = document.getElementById('status-' + questionId);
        
        if (textarea && charCountSpan) {
            const text = textarea.value;
            const charCount = text.length;
            charCountSpan.textContent = charCount;
            
            // Update answer status
            if (text.trim().length > 0) {
                if (!answeredQuestions.has(questionId)) {
                    answeredQuestions.add(questionId);
                    updateOverallProgress();
                }
                statusSpan.textContent = "Answered";
                statusSpan.className = "answer-status answered";
            } else {
                if (answeredQuestions.has(questionId)) {
                    answeredQuestions.delete(questionId);
                    updateOverallProgress();
                }
                statusSpan.textContent = "Not answered";
                statusSpan.className = "answer-status not-answered";
            }
        }
    }
    
    // Update overall progress indicator
    function updateOverallProgress() {
        const totalQuestions = <%= questions.size() %>;
        const answeredCount = answeredQuestions.size;
        const progressSpan = document.getElementById('answeredCount');
        if (progressSpan) {
            progressSpan.textContent = answeredCount;
        }
    }
    
    // Submit individual question
    function submitQuestion(questionId) {
        const textarea = document.getElementById(questionId);
        const answer = textarea.value.trim();
        const feedbackDiv = document.getElementById('feedback-' + questionId);
        const questionCard = document.querySelector(`[data-question-id="${questionId}"]`);
        const questionTitle = questionCard.querySelector('.question-title h3').textContent;
        
        if (!answer) {
            showFeedback(feedbackDiv, 'Please enter an answer before submitting.', 'error');
            return;
        }
        
        // Disable submit button during submission
        const submitBtn = event.target;
        const originalText = submitBtn.textContent;
        submitBtn.disabled = true;
        submitBtn.textContent = 'Submitting...';
        
        // Simulate AJAX submission (in real app, this would send to server)
        setTimeout(() => {
            // Create FormData for submission
            const formData = new FormData();
            formData.append('questionId', questionId);
            formData.append('answer', answer);
            formData.append('questionTitle', questionTitle);
            
            // Simulate successful submission
            showFeedback(feedbackDiv, 'Answer submitted successfully! ✓', 'success');
            
            // Mark as submitted (add visual indicator)
            questionCard.classList.add('submitted');
            
            // Re-enable button
            submitBtn.disabled = false;
            submitBtn.textContent = originalText;
            
            // In a real application, you would make an AJAX call here:
            /*
            fetch('submitAnswer', {
                method: 'POST',
                body: formData
            })
            .then(response => response.json())
            .then(data => {
                if (data.success) {
                    showFeedback(feedbackDiv, 'Answer submitted successfully!', 'success');
                } else {
                    showFeedback(feedbackDiv, 'Error submitting answer. Please try again.', 'error');
                }
            })
            .catch(error => {
                showFeedback(feedbackDiv, 'Network error. Please check your connection.', 'error');
            })
            .finally(() => {
                submitBtn.disabled = false;
                submitBtn.textContent = originalText;
            });
            */
        }, 500);
    }
    
    // Clear answer for a question
    function clearAnswer(questionId) {
        const textarea = document.getElementById(questionId);
        const feedbackDiv = document.getElementById('feedback-' + questionId);
        
        if (textarea) {
            textarea.value = '';
            updateAnswerStatus(questionId);
            
            // Remove submitted class from card
            const questionCard = document.querySelector(`[data-question-id="${questionId}"]`);
            questionCard.classList.remove('submitted');
            
            // Clear feedback
            feedbackDiv.innerHTML = '';
            feedbackDiv.className = 'submission-feedback';
            
            // Show temporary clear message
            showFeedback(feedbackDiv, 'Answer cleared', 'info');
            setTimeout(() => {
                if (feedbackDiv.innerHTML === 'Answer cleared') {
                    feedbackDiv.innerHTML = '';
                    feedbackDiv.className = 'submission-feedback';
                }
            }, 2000);
        }
    }
    
    // Show feedback message
    function showFeedback(element, message, type) {
        element.innerHTML = message;
        element.className = 'submission-feedback ' + type;
        
        // Auto-hide success/error messages after 5 seconds
        if (type !== 'info') {
            setTimeout(() => {
                if (element.innerHTML === message) {
                    element.innerHTML = '';
                    element.className = 'submission-feedback';
                }
            }, 5000);
        }
    }
    
    // Handle logout
    function handleLogout() {
        if (confirm('Are you sure you want to logout?')) {
            window.location.href = 'logout';
        }
    }
    
    // Auto-save answers to localStorage
    function autoSaveAnswers() {
        const textareas = document.querySelectorAll('.answer-textbox');
        const answers = {};
        
        textareas.forEach(textarea => {
            answers[textarea.id] = textarea.value;
        });
        
        localStorage.setItem('codex2_answers', JSON.stringify(answers));
        localStorage.setItem('codex2_timestamp', new Date().toISOString());
    }
    
    // Load saved answers from localStorage
    function loadSavedAnswers() {
        const savedAnswers = localStorage.getItem('codex2_answers');
        if (savedAnswers) {
            const answers = JSON.parse(savedAnswers);
            for (const [id, value] of Object.entries(answers)) {
                const textarea = document.getElementById(id);
                if (textarea && value) {
                    textarea.value = value;
                    updateAnswerStatus(id);
                }
            }
        }
    }
    
    // Auto-save every 30 seconds
    setInterval(autoSaveAnswers, 30000);
    
    // Save on page unload
    window.addEventListener('beforeunload', () => {
        autoSaveAnswers();
    });
    
    // Load saved answers when page loads
    document.addEventListener('DOMContentLoaded', () => {
        loadSavedAnswers();
        updateOverallProgress();
        
        // Add keyboard shortcut (Ctrl+Enter to submit current question)
        document.addEventListener('keydown', (e) => {
            if (e.ctrlKey && e.key === 'Enter') {
                e.preventDefault();
                const activeElement = document.activeElement;
                if (activeElement && activeElement.classList && activeElement.classList.contains('answer-textbox')) {
                    const questionId = activeElement.id;
                    const submitBtn = document.querySelector(`[data-question-id="${questionId}"] .submit-question-btn`);
                    if (submitBtn) {
                        submitBtn.click();
                    }
                }
            }
        });
    });
</script>

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