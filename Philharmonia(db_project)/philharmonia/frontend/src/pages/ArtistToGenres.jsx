import React, {useCallback, useEffect, useMemo, useState} from 'react';
import {MaterialReactTable} from "material-react-table";
import {Box, Button, IconButton, Tooltip} from "@mui/material";
import {Delete} from "@mui/icons-material";
import CreateModal from "../components/UI/CreateModal";
import {addArtistToGenre, deleteArtistToGenre, getArtistToGenres} from "../services/ArtistService";

const ArtistToGenres = () => {
    const columns = useMemo(() => [
        {
            header: "Имя",
            accessorKey: "artist.name",
            enableEditing: false,
        },
        {
            header: "Фамилия",
            accessorKey: "artist.surname",
        },
        {
            header: "Жанр",
            accessorKey: "genre.name",
        }
    ], []);

    const [createModalOpen, setCreateModalOpen] = useState(false);
    const [distribution, setDistribution] = useState([]);
    const [validationErrors, setValidationErrors] = useState({});


    const handleCreateNewRow = (values) => {
        const data = {
            artist: {
                name: values["artist.name"],
                surname: values["artist.surname"],
            },
            genre: {
                name: values["genre.name"],
            },
        }
        addArtistToGenre(data).then(() => refreshData());
    };

    const refreshData = () => {
        getArtistToGenres().then(genres => {
            setDistribution(genres);
            console.log(genres);
        })
    };

    const handleDeleteRow = useCallback(
        (row) => {
            if (!confirm(`Are you sure?`)) {
                return;
            }
            const data = {
                artist: {
                    name: distribution[row.index]["artist.name"],
                    surname: distribution[row.index]["artist.surname"],
                },
                genre: {
                    name: genre,
                },
            }
            deleteArtistToGenre(data).then(() => refreshData());
        },
        [],
    );

    const handleCancelRowEdits = () => {
        setValidationErrors({});
    };


    useEffect(() => {
        refreshData();
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
                    data={distribution}
                    enableEditing={true}
                    onEditingRowCancel={handleCancelRowEdits}
                    renderRowActions={({row, table}) => (
                        <Box sx={{display: 'flex', gap: '1rem'}}>
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
                            Добавить связь артист-жанр
                        </Button>
                    )}
                />
            </div>
            <CreateModal
                columns={columns}
                open={createModalOpen}
                onClose={() => setCreateModalOpen(false)}
                onSubmit={handleCreateNewRow}
            />
        </div>
    )
};

export default ArtistToGenres;
