import React, {useCallback, useEffect, useMemo, useState} from 'react';
import {addImpresario, deleteImpresario, getAllImpresarios, updateImpresario} from "../services/ImpresarioService";
import {Box, Button, IconButton, Tooltip} from "@mui/material";
import {Delete, Edit} from "@mui/icons-material";
import {MaterialReactTable} from "material-react-table";
import CreateModal from "../components/UI/CreateModal";

const Impresarios = () => {
    const columns = useMemo(() => [
        {
            header: "ID",
            accessorKey: "id",
            enableEditing: false,
        },
        {
            header: "Имя",
            accessorKey: "name",
        },
        {
            header: "Фамилия",
            accessorKey: "surname",
        }
    ], []);

    const [createModalOpen, setCreateModalOpen] = useState(false);
    const [impresarios, setImpresarios] = useState([]);
    const [validationErrors, setValidationErrors] = useState({});


    const handleCreateNewRow = (values) => {
        addImpresario({...values, id: 0}).then(() => refreshData());
    };

    const handleSaveRowEdits = async ({exitEditingMode, row, values}) => {
        if (!Object.keys(validationErrors).length) {
            impresarios[row.index] = values;
            console.log(values);
            updateImpresario(values).then(() => refreshData());
            exitEditingMode(); //required to exit editing mode and close modal
        }
    };

    const handleDeleteRow = useCallback(
        (row) => {
            if (!confirm(`Are you sure you want to delete ${row.getValue("name")} ${row.getValue("surname")}`)) {
                return;
            }
            deleteImpresario(row.getValue("id")).then(() => refreshData());
        },
        [],
    );

    const handleCancelRowEdits = () => {
        setValidationErrors({});
    };

    const refreshData = () => {
        getAllImpresarios().then(impresarios => {
            setImpresarios(impresarios);
            console.log(impresarios);
        })
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
                    data={impresarios}
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
                            Добавить импресарио
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
    );
};

export default Impresarios;
