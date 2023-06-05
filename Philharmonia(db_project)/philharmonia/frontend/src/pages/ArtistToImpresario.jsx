import React, {useCallback, useEffect, useMemo, useState} from 'react';
import {MaterialReactTable} from "material-react-table";
import {Box, Button, IconButton, Tooltip} from "@mui/material";
import {Delete} from "@mui/icons-material";
import CreateModal from "../components/UI/CreateModal";
import {addArtistToImpresario, getArtistToImpresario} from "../services/ImpresarioService";

const ArtistToImpresario = () => {
    const columns = useMemo(() => [
        {
            header: "Имя артиста",
            accessorKey: "artist.name",
            enableEditing: false,
        },
        {
            header: "Фамилия артиста",
            accessorKey: "artist.surname",
            enableEditing: false,
        },
        {
            header: "Имя импресарио",
            accessorKey: "impresario.name",
            enableEditing: false,
        },
        {
            header: "Фамилия импресарио",
            accessorKey: "impresario.surname",
            enableEditing: false,
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
            impresario: {
                name: values["impresario.name"],
                surname: values["impresario.surname"],
            },
        }
        addArtistToImpresario(data).then(() => refreshData());
    };

    const refreshData = () => {
        getArtistToImpresario().then(genres => {
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
                impresario: {
                    name: distribution[row.index]["impresario.name"],
                    surname: distribution[row.index]["impresario.surname"],
                },
            }
            deleteArtistToImpresario(data).then(() => refreshData());
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
                    // onEditingRowCancel={handleCancelRowEdits}
                    // renderRowActions={({row, table}) => (
                    //     <Box sx={{display: 'flex', gap: '1rem'}}>
                    //         <Tooltip arrow placement="right" title="Delete">
                    //             <IconButton color="error" onClick={() => handleDeleteRow(row)}>
                    //                 <Delete/>
                    //             </IconButton>
                    //         </Tooltip>
                    //     </Box>
                    // )}
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

export default ArtistToImpresario;
