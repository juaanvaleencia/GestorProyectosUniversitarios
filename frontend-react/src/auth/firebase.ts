import { initializeApp, type FirebaseApp } from 'firebase/app';
import {
  getAuth,
  signInWithEmailAndPassword,
  createUserWithEmailAndPassword,
  signOut,
  onAuthStateChanged,
  type User,
} from 'firebase/auth';

const config = {
  apiKey: import.meta.env.VITE_FIREBASE_API_KEY,
  authDomain: import.meta.env.VITE_FIREBASE_AUTH_DOMAIN,
  projectId: import.meta.env.VITE_FIREBASE_PROJECT_ID,
  storageBucket: import.meta.env.VITE_FIREBASE_STORAGE_BUCKET,
  messagingSenderId: import.meta.env.VITE_FIREBASE_MESSAGING_SENDER_ID,
  appId: import.meta.env.VITE_FIREBASE_APP_ID,
};

export const firebaseConfigured = Boolean(config.apiKey && config.projectId);

let app: FirebaseApp | null = null;

export function getFirebaseAuth() {
  if (!firebaseConfigured) return null;
  if (!app) {
    app = initializeApp(config);
  }
  return getAuth(app);
}

export async function loginEmail(email: string, password: string) {
  const auth = getFirebaseAuth();
  if (!auth) throw new Error('Firebase no configurado. Copia .env.example a .env');
  return signInWithEmailAndPassword(auth, email, password);
}

export async function registerEmail(email: string, password: string) {
  const auth = getFirebaseAuth();
  if (!auth) throw new Error('Firebase no configurado');
  return createUserWithEmailAndPassword(auth, email, password);
}

export async function logout() {
  const auth = getFirebaseAuth();
  if (auth) await signOut(auth);
}

export function subscribeAuth(callback: (user: User | null) => void) {
  const auth = getFirebaseAuth();
  if (!auth) {
    callback(null);
    return () => {};
  }
  return onAuthStateChanged(auth, callback);
}

export async function getIdToken(): Promise<string | null> {
  const auth = getFirebaseAuth();
  const user = auth?.currentUser;
  if (!user) return null;
  return user.getIdToken();
}
