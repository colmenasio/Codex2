console.log("meow :3")

const form = document.getElementById('loginForm');
const errorDiv = form.querySelector('.error-message');

form.addEventListener('submit', async (e) => {
    e.preventDefault();

    // Hide previous error
    errorDiv.style.display = 'none';
    errorDiv.textContent = '';

    // Disable button to prevent double submission
    const submitBtn = form.querySelector('.primary-btn');
    submitBtn.disabled = true;
    submitBtn.textContent = 'Signing in...';

    const payload = {
        action: "signin",
        username: document.getElementById('username').value,
        password: document.getElementById('password').value
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

        if (!response.ok) {
            // Non-success response or missing token
            throw new Error('Unknown error');
        }

        const data = await response.json().catch(() => ({}));

        if (data.ok !== true) {
            throw new Error(data.error_msg == undefined ? "Unknown error" : data.error_msg);
        }

        console.log('Login successful, token:', data.token);
        localStorage.setItem('authToken', data.token);

        window.location.href = '/home'; // redirect xd

    } catch (error) {
        // Display error message
        errorDiv.textContent = error.message;
        errorDiv.style.display = 'block';

        // Re-enable button
        submitBtn.disabled = false;
        submitBtn.textContent = 'Sign in';
    }
});