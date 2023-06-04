import React, {useCallback, useEffect, useMemo, useState} from 'react';
import {addGenre, deleteGenre, getAllGenres, updateGenre} from "../services/GenreService";
import {MaterialReactTable} from "material-react-table";
import {Box, Button, IconButton, Tooltip,} from "@mui/material";
import {Delete, Edit} from '@mui/icons-material';
import CreateModal from "../components/UI/CreateModal";

const Genres = () => {

    const columns = useMemo(() => [
        {
            header: "ID",
            accessorKey: "id",
            enableEditing: false,
        },
        {
            header: "Название",
            accessorKey: "name",
        }
    ], []);

    const [createModalOpen, setCreateModalOpen] = useState(false);
    const [genres, setGenres] = useState([]);
    const [validationErrors, setValidationErrors] = useState({});


    const handleCreateNewRow = (values) => {
        //todo here is post request and then update table
        addGenre({id: 0, name: values.name}).then(() => refreshData());
    };

    const refreshData = () => {
        getAllGenres().then(genres => {
            setGenres(genres);
            console.log(genres);
        })
    };

    const handleSaveRowEdits = async ({exitEditingMode, row, values}) => {
        if (!Object.keys(validationErrors).length) {
            genres[row.index] = values;
            console.log(values);
            updateGenre(values).then(() => refreshData());
            //todo send/receive api updates here, then refetch or update local table data for re-render
            exitEditingMode(); //required to exit editing mode and close modal
        }
    };

    const handleDeleteRow = useCallback(
        (row) => {
            if (!confirm(`Are you sure you want to delete ${row.getValue("name")}`)) {
                return;
            }
            deleteGenre(row.getValue("id")).then(() => refreshData());
            //todo send api delete request here, then refetch or update local table data for re-render
            // genres.splice(row.index, 1);
            // setGenres([...genres]);
        },
        [],
    );

    const handleCancelRowEdits = () => {
        setValidationErrors({});
    };


    useEffect(() => {
        getAllGenres().then(genres => {
            setGenres(genres);
            console.log(genres);
        })
    }, [])


    return (
        <div>
            <div>
                <MaterialReactTable
                    displayColumnDefOptions={{
                        'mrt-row-actions': {
                            size: 60,
                        },
                    }}
                    columns={columns}
                    data={genres}
                    editingMode={"modal"}
                    enableEditing={true}
                    onEditingRowSave={handleSaveRowEdits}
                    onEditingRowCancel={handleCancelRowEdits}
                    renderRowActions={({row, table}) => (
                        <Box sx={{display: 'flex', gap: '1rem'}}>
                            <Tooltip arrow placement="left" title="Edit">
                                <IconButton onClick={() => table.setEditingRow(row)}>
                                    <Edit/>
                                </IconButton>
                            </Tooltip>
                            <Tooltip arrow placement="right" title="Delete">
                                <IconButton color="error" onClick={() => handleDeleteRow(row)}>
                                    <Delete/>
                                </IconButton>
                            </Tooltip>
                        </Box>
                    )}
                    renderTopToolbarCustomActions={() => (
                        <Button
                            color="secondary"
                            onClick={() => setCreateModalOpen(true)}
                            variant="contained"
                        >
                            Добавить новый жанр
                        </Button>
                    )}
                />
            </div>
            <CreateModal
                // columns={columns.filter((cols, i) => i > 0)}
                columns={columns}
                open={createModalOpen}
                onClose={() => setCreateModalOpen(false)}
                onSubmit={handleCreateNewRow}
            />
        </div>
    )
};

export default Genres;
