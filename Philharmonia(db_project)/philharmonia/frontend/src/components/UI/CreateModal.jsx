import {useState} from "react";
import {Button, Dialog, DialogActions, DialogContent, DialogTitle, Stack, TextField} from "@mui/material";

const CreateModal = ({open, columns, onClose, onSubmit}) => {
    const [values, setValues] = useState(() =>
        columns.reduce((acc, column) => {
            acc[column.accessorKey ?? ''] = '';
            return acc;
        }, {}),
    );

    const handleSubmit = () => {
        //todo put your validation logic here
        onSubmit(values);
        onClose();
    };

    return (
        <Dialog open={open}>
            <DialogTitle textAlign="center">Добавить новый жанр</DialogTitle>
            <DialogContent>
                <form onSubmit={(e) => e.preventDefault()}>
                    <Stack
                        sx={{
                            width: '100%',
                            minWidth: {xs: '300px', sm: '360px', md: '400px'},
                            gap: '1.5rem',
                        }}
                    >
                        {/*{columns.filter((col, i) => i > 0).map(col => (*/}
                        {/*    <TextField*/}
                        {/*        key={column.accessorKey}*/}
                        {/*        label={column.header}*/}
                        {/*        name={column.accessorKey}*/}
                        {/*        onChange={(e) =>*/}
                        {/*            setValues({...values, [e.target.name]: e.target.value})*/}
                        {/*        }*/}
                        {/*    />*/}
                        {/*))}*/}
                        {columns.map((column, i) => (
                            i !== 0 && <TextField
                                key={column.accessorKey}
                                label={column.header}
                                name={column.accessorKey}
                                onChange={(e) =>
                                    setValues({...values, [e.target.name]: e.target.value})
                                }
                            />
                        ))}
                    </Stack>
                </form>
            </DialogContent>
            <DialogActions sx={{p: '1.25rem'}}>
                <Button onClick={onClose}>Отмена</Button>
                <Button color="secondary" onClick={handleSubmit} variant="contained">
                    Добавить жанр
                </Button>
            </DialogActions>
        </Dialog>
    );
};

export default CreateModal;