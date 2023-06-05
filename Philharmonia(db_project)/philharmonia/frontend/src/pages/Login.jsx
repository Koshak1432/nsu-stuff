import React, {useContext, useState} from 'react';
import MyInput from "../components/UI/input/MyInput";
import MyButton from "../components/UI/button/MyButton";
import {AuthContext} from "../components/context/AuthContext";

const Login = () => {
    const {isAuth, setIsAuth} = useContext(AuthContext);
    const [log, setLog] = useState("");
    const [password, setPassword] = useState("");
    const login = event => {
        event.preventDefault();
        if (log === "admin" && password === "admin") {
            setIsAuth(true);
            localStorage.setItem("auth", "true");
        }
    }

    return (
        <div>
            <h1>Авторизация</h1>
            <form onSubmit={login}>
                <MyInput value={log} onChange={e => setLog(e.target.value)} type={"text"} placeholder={"Введите логин"}/>
                <MyInput value={password} onChange={e => setPassword(e.target.value)} type={"password"} placeholder={"Введите пароль"}/>
                <MyButton>Войти</MyButton>
            </form>
        </div>
    );
};

export default Login;
