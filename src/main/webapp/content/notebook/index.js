
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

