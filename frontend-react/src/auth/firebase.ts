import { initializeApp, type FirebaseApp, FirebaseError } from 'firebase/app';
import {
  getAuth,
  signInWithEmailAndPassword,
  createUserWithEmailAndPassword,
  signOut,
  onAuthStateChanged,
  updateProfile,
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

export async function registerEmail(email: string, password: string, nombre: string) {
  const auth = getFirebaseAuth();
  if (!auth) throw new Error('Firebase no configurado');
  const cred = await createUserWithEmailAndPassword(auth, email, password);
  const nombreLimpio = nombre.trim();
  if (nombreLimpio) {
    await updateProfile(cred.user, { displayName: nombreLimpio });
  }
  return cred;
}

export function isEmailAlreadyInUse(err: unknown): boolean {
  return err instanceof FirebaseError && err.code === 'auth/email-already-in-use';
}

export async function registerOrLoginEmail(email: string, password: string, nombre: string) {
  try {
    return await registerEmail(email, password, nombre);
  } catch (err) {
    if (!isEmailAlreadyInUse(err)) throw err;
    return loginEmail(email, password);
  }
}

export async function updateUserProfile(
  user: User,
  data: { displayName?: string; photoURL?: string | null },
) {
  const auth = getFirebaseAuth();
  if (!auth) throw new Error('Firebase no configurado');
  await updateProfile(user, {
    displayName: data.displayName,
    photoURL: data.photoURL ?? null,
  });
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
