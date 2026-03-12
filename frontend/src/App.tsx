/**
 * 应用根组件
 */

import { useState } from 'react';
import LoginPage from './pages/LoginPage';
import RegisterPage from './pages/RegisterPage';

export default function App() {
  const [currentView, setCurrentView] = useState<'login' | 'register'>('login');

  if (currentView === 'register') {
    return <RegisterPage onSwitchToLogin={() => setCurrentView('login')} />;
  }

  return <LoginPage onSwitchToRegister={() => setCurrentView('register')} />;
}
