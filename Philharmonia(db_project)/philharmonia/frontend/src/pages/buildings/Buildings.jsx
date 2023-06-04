import React, {useCallback, useEffect, useMemo, useState} from 'react';
import {addBuilding, deleteBuilding, getAllBuildings, updateBuilding} from "../../services/BuildingService";
import {Box, Button, IconButton, Tooltip, Typography} from "@mui/material";
import {Delete, Edit} from "@mui/icons-material";
import {MaterialReactTable} from "material-react-table";
import CreateModal from "../../components/UI/CreateModal";

const Buildings = () => {

    const columns = useMemo(() => [
        {
            header: "ID",
            accessorKey: "id",
            enableEditing: false,
        },
        {
            header: "Название",
            accessorKey: "name",
        },
        {
            header: "Тип",
            accessorKey: "typeName",
        }
    ], []);

    const [createModalOpen, setCreateModalOpen] = useState(false);
    const [buildings, setBuildings] = useState([]);
    const [validationErrors, setValidationErrors] = useState({});
    const [specificTypes, setSpecificTypes] = useState([]);

    const handleCreateNewRow = (values) => {
        addBuilding({...values, id: 0}).then(() => refreshData());
    };

    const handleSaveRowEdits = async ({exitEditingMode, row, values}) => {
        if (!Object.keys(validationErrors).length) {
            buildings[row.index] = values;
            console.log(values);
            updateBuilding(values).then(() => refreshData());
            exitEditingMode(); //required to exit editing mode and close modal
        }
    };

    const handleDeleteRow = useCallback(
        (row) => {
            if (!confirm(`Are you sure you want to delete ${row.getValue("name")}`)) {
                return;
            }
            deleteBuilding(row.getValue("id")).then(() => refreshData());
        },
        [],
    );

    const handleCancelRowEdits = () => {
        setValidationErrors({});
    };

    const refreshData = () => {
        getAllBuildings().then(buildings => {
            setBuildings(buildings);
            console.log(buildings);
        })
    };


    useEffect(() => {
        refreshData();
    }, [])

    //todo тут же вставить кнопки посмотреть театры, эстрады и прочее и отфильтровать таблицу
    return (
        <div>
            <div>
                <h2>Сооружения</h2>
                <MaterialReactTable
                    displayColumnDefOptions={{
                        'mrt-row-actions': {
                            size: 60,
                        },
                    }}
                    columns={columns}
                    data={buildings}
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
                            Добавить сооружение
                        </Button>
                    )}
                    renderDetailPanel={({ row }) => (
                        <Box
                            sx={{
                                display: 'grid',
                                margin: 'auto',
                                gridTemplateColumns: '1fr 1fr',
                                width: '100%',
                            }}
                        >
                            <div>{row.index}</div>
                        </Box>
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

export default Buildings;
