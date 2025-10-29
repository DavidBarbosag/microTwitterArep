const COGNITO_DOMAIN = "us-east-1dechij83m.auth.us-east-1.amazoncognito.com";
const CLIENT_ID = "57ffro7esnoed2adknd2em6nh8";
const REGION = "us-east-1";
const REDIRECT_URI = "http://localhost:3000";
const API_BASE = "http://localhost:8080";

const LOGIN_URL = `https://${COGNITO_DOMAIN}/login?client_id=${CLIENT_ID}&response_type=token&scope=email+openid+profile&redirect_uri=${REDIRECT_URI}`;
const LOGOUT_URL = `https://${COGNITO_DOMAIN}/logout?client_id=${CLIENT_ID}&logout_uri=${REDIRECT_URI}`;

function getTokenFromHash() {
    const hash = window.location.hash.substring(1);
    const params = new URLSearchParams(hash);
    return params.get("access_token");
}

function saveToken(token) {
    localStorage.setItem("token", token);
}

function getToken() {
    return localStorage.getItem("token");
}

function logout() {
    localStorage.removeItem("token");
    window.location.href = LOGOUT_URL;
}

async function fetchPosts() {
    const res = await fetch(`${API_BASE}/api/streams/global/posts`);
    return res.json();
}

async function createPost(text) {
    const token = getToken();
    if (!token) return alert("No estás autenticado");
    const res = await fetch(`${API_BASE}/api/streams/global/posts`, {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
            "Authorization": `Bearer ${token}`,
        },
        body: JSON.stringify({ text }),
    });
    return res.json();
}

function renderLogin() {
    const app = document.getElementById("app");
    app.innerHTML = `
    <div class="login">
      <h1>MicroTwitter</h1>
      <a class="btn" href="${LOGIN_URL}">Iniciar sesión con Cognito</a>
    </div>
  `;
}

function renderTimeline() {
    const app = document.getElementById("app");
    app.innerHTML = `
    <header>
      <h1>MicroTwitter</h1>
      <button id="logoutBtn" class="logout">Cerrar sesión</button>
    </header>

    <form id="postForm" class="form">
      <textarea id="postText" maxlength="140" placeholder="¿Qué está pasando?"></textarea>
      <button type="submit" class="btn">Publicar</button>
    </form>

    <div id="posts" class="posts">Cargando...</div>
  `;

    document.getElementById("logoutBtn").onclick = logout;

    const postForm = document.getElementById("postForm");
    postForm.addEventListener("submit", async (e) => {
        e.preventDefault();
        const text = document.getElementById("postText").value.trim();
        if (text.length === 0) return;
        await createPost(text);
        document.getElementById("postText").value = "";
        loadPosts();
    });

    loadPosts();
}

async function loadPosts() {
    const postsDiv = document.getElementById("posts");
    postsDiv.innerHTML = "Cargando...";
    const posts = await fetchPosts();
    if (!Array.isArray(posts)) {
        postsDiv.innerHTML = "<p>Error cargando posts</p>";
        return;
    }
    postsDiv.innerHTML = posts.map(p => `
    <div class="post">
      <p>${p.text}</p>
      <small>${p.author?.username || "Anónimo"}</small>
    </div>
  `).join("");
}

// ====== INICIO ======
(function init() {
    const newToken = getTokenFromHash();
    if (newToken) {
        saveToken(newToken);
        window.location.hash = "";
    }

    const token = getToken();
    if (!token) {
        renderLogin();
    } else {
        renderTimeline();
    }
})();
