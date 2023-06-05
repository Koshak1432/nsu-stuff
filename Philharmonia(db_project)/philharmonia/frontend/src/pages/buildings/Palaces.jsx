import React, {useCallback, useEffect, useMemo, useState} from 'react';
import {addPalace, deleteBuilding, getAllPalaces, updatePalace} from "../../services/BuildingService";
import {MaterialReactTable} from "material-react-table";
import {Box, Button, IconButton, Tooltip} from "@mui/material";
import {Delete, Edit} from "@mui/icons-material";
import CreateModal from "../../components/UI/CreateModal";

const Palaces = () => {
    const columns = useMemo(() => [
        {
            header: "ID",
            accessorKey: "building.id",
            enableEditing: false,
        },
        {
            header: "Название",
            accessorKey: "building.name",
        },
        {
            header: "Количество этажей",
            accessorKey: "floorNum",
        }
    ], []);

    const [createModalOpen, setCreateModalOpen] = useState(false);
    const [palaces, setPalaces] = useState([]);
    const [validationErrors, setValidationErrors] = useState({});

    const handleCreateNewRow = (values) => {
        const data = {
            building: {
                id: values["building.id"],
                name: values["building.name"],
            },
            floorNum: values["floorNum"],
        }
        addPalace(data).then(() => refreshData());
    };

    const handleSaveRowEdits = async ({exitEditingMode, row, values}) => {
        if (!Object.keys(validationErrors).length) {
            console.log(values);
            updatePalace(values).then(() => refreshData());
            exitEditingMode(); //required to exit editing mode and close modal
        }
    };

    const handleDeleteRow = useCallback(
        (row) => {
            if (!confirm(`Are you sure?}`)) {
                return;
            }
            deleteBuilding(row.getValue("building.id")).then(() => refreshData());
        },
        [],
    );

    const handleCancelRowEdits = () => {
        setValidationErrors({});
    };

    const refreshData = () => {
        getAllPalaces().then(palaces => {
            setPalaces(palaces);
            console.log(palaces);
        })
    };


    useEffect(() => {
        refreshData();
    }, [])

    return (
        <div>
            <div>
                <h2>Дворцы культуры</h2>
                <MaterialReactTable
                    displayColumnDefOptions={{
                        'mrt-row-actions': {
                            size: 60,
                        },
                    }}
                    columns={columns}
                    data={palaces}
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
                            Добавить дворец
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

export default Palaces;
