import React from 'react';
import classes from './ModalWindow.module.css'

const ModalWindow = ({children, visible, setClose}) => {
    const rootClasses = [classes.myModal];
    if (visible) {
        rootClasses.push(classes.active);
    }

    return (
        <div className={rootClasses.join(' ')} onClick={() => setClose()} >
            <div className={classes.modalContent} onClick={(e) => e.stopPropagation()}>
                {children}
            </div>
        </div>
    );
};

export default ModalWindow;
