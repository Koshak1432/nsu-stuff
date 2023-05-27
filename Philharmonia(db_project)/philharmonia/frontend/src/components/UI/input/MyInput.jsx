import React from 'react';
import classes from './MyInput.module.css'

const MyComponent = (props) => {
    return (
        <input className={classes.myInput} {...props}/>
    );
};

export default MyComponent;
