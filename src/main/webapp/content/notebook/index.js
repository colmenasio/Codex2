
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

    setTimeout(() => {

        const payload = {
            action: "submit_answer",
            authToken: localStorage.getItem("authToken"),
            questionId: parseInt(questionId.replace("question", "")),
            answer: answer
        };

        fetch(window.location.pathname, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Accept': 'application/json'
            },
            body: JSON.stringify(payload)
        })
            .then(response => response.json())
            .then(data => {
                if (data.ok !== true || data.is_correct == undefined) {
                    console.log(data.error_msg == undefined ? "Unknown error" : data.error_msg);
                }
                if (data.is_correct === true) {
                    showFeedback(feedbackDiv, 'Good Answer Nephew!', 'success');
                    questionCard.classList.add('submitted');
                    questionCard.classList.remove('wrong');
                } else {
                    showFeedback(feedbackDiv, data.correction_msg === undefined ? '[LOUD INCORRECT BUZZER]' : data.correction_msg, 'error');
                    questionCard.classList.add('wrong');
                    questionCard.classList.add('submitted');
                }
            })
            .catch(error => {
                showFeedback(feedbackDiv, 'Unknown error', 'error');
            })
            .finally(() => {
                submitBtn.disabled = false;
                submitBtn.textContent = originalText;
            });

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
        questionCard.classList.remove('wrong');

        // Clear feedback
        feedbackDiv.innerHTML = '';
        feedbackDiv.className = 'submission-feedback';
    }
}

function clearAnswers() {
    setTimeout(() => {

        const payload = {
            action: "delete_stored_answers",
            authToken: localStorage.getItem("authToken"),
        };

        fetch(window.location.pathname, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Accept': 'application/json'
            },
            body: JSON.stringify(payload)
        })
            .then(response => response.json())
            .then(data => {
                if (data.ok !== true) {
                    console.log(data.error_msg == undefined ? "Unknown error" : data.error_msg);
                }
                for (let i = 0; i < totalQuestions; i++) {
                    clearAnswer("question" + i);
                }
            })
            .catch(error => {
                showFeedback(feedbackDiv, 'Unknown error', 'error');
            })
            .finally(() => {
                submitBtn.disabled = false;
                submitBtn.textContent = originalText;
            });

    }, 500);

}

// Show feedback message
function showFeedback(element, message, type) {
    element.innerHTML = message;
    element.className = 'submission-feedback ' + type;
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
            return;
        }
    }
}

async function loadSavedAnswers() {
    const payload = {
        action: "request_stored_answers",
        authToken: localStorage.getItem("authToken"),
    };

    fetch(window.location.pathname, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'Accept': 'application/json'
        },
        body: JSON.stringify(payload)
    })
        .then(response => response.json())
        .then(data => {
            if (data.ok !== true) {
                console.log(data.error_msg || "Failed to load saved answers");
                return;
            }

            if (data.data && typeof data.data === 'object') {
                for (const [questionNumber, answerData] of Object.entries(data.data)) {
                    const questionId = `question${questionNumber}`;
                    const textarea = document.getElementById(questionId);

                    if (textarea && answerData.raw_answer) {
                        textarea.value = answerData.raw_answer;

                        updateAnswerStatus(questionId);

                        const questionCard = document.querySelector(`[data-question-id="${questionId}"]`);
                        if (questionCard && answerData.is_correct === true) {
                            questionCard.classList.add('submitted');

                            const statusSpan = document.getElementById(`status-${questionId}`);
                            if (statusSpan) {
                                statusSpan.textContent = "Answered (correct)";
                                statusSpan.className = "answer-status answered correct";
                            }
                        } else if (questionCard && answerData.raw_answer && answerData.raw_answer.trim()) {
                            // Answer exists but was incorrect
                            questionCard.classList.add('wrong');
                            const statusSpan = document.getElementById(`status-${questionId}`);
                            if (statusSpan) {
                                statusSpan.textContent = "Answered (needs review)";
                                statusSpan.className = "answer-status not-answered needs-review";
                            }
                        }
                    }
                }
                updateOverallProgress();
                console.log(`Loaded ${Object.keys(data.data).length} saved answers`);
            }
        })
        .catch(error => {
            console.error("Error loading saved answers:", error);
        });
}

async function forget_answers() {
    // TODO mandar un request que borre la entrada de la dabeis
}

setInterval(updateOverallProgress, 1000);

// Load saved answers when page loads
document.addEventListener('DOMContentLoaded', async () => {
    await loadSavedAnswers();
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

