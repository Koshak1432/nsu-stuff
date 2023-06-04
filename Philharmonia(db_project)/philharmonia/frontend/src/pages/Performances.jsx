import React, {useCallback, useEffect, useMemo, useState} from 'react';
import {addPerformance, deletePerformance, getAllPerformances, updatePerformance} from "../services/PerformanceService";
import {MaterialReactTable} from "material-react-table";
import {Box, Button, IconButton, Tooltip} from "@mui/material";
import {Delete, Edit} from "@mui/icons-material";
import CreateModal from "../components/UI/CreateModal";

const Performances = () => {
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
            header: "Тип",
            accessorKey: "typeName",
        },
        {
            header: "Дата проведения",
            accessorKey: "performanceDate",
        },
        {
            header: "Имя организатора",
            accessorKey: "sponsor.name",
        },
        {
            header: "Фамилия организатора",
            accessorKey: "sponsor.surname",
        },
        {
            header: "Название сооружения",
            accessorKey: "building.name",
        },
        {
            header: "Тип сооружения",
            accessorKey: "building.typeName",
        }
    ], []);

    const [performances, setPerformances] = useState([]);
    const [createModalOpen, setCreateModalOpen] = useState(false);
    const [validationErrors, setValidationErrors] = useState({});


    const handleCreateNewRow = (values) => {

        const performance = {
            id: 0,
            name: values.name,
            sponsor: {
                name: values["sponsor.name"],
                surname: values["sponsor.surname"],
            },
            building: {
                name: values["building.name"],
                typeName: values["building.typeName"],
            },
            performanceDate: values.performanceDate,
            typeName: values.typeName
        }
        console.log(performance);

        addPerformance(performance).then(() => refreshData());
    };

    const handleSaveRowEdits = async ({exitEditingMode, row, values}) => {
        if (!Object.keys(validationErrors).length) {
            performances[row.index] = values;
            console.log(values);
            const performance = {
                id: values.id,
                name: values.name,
                sponsor: {
                    name: values["sponsor.name"],
                    surname: values["sponsor.surname"],
                },
                building: {
                    name: values["building.name"],
                    typeName: values["building.typeName"],
                },
                performanceDate: values.performanceDate,
                typeName: values.typeName
            }
            updatePerformance(performance).then(() => refreshData());
            exitEditingMode(); //required to exit editing mode and close modal
        }
    };

    const handleDeleteRow = useCallback(
        (row) => {
            if (!confirm(`Are you sure you want to delete ${row.getValue("name")}`)) {
                return;
            }
            deletePerformance(row.getValue("id")).then(() => refreshData());
        },
        [],
    );

    const handleCancelRowEdits = () => {
        setValidationErrors({});
    };

    const refreshData = () => {
        getAllPerformances().then(performances => {
            setPerformances(performances);
            console.log(performances);
        })
    };

    useEffect(() => {
        refreshData();
    }, [])

    return (
        <div>
            <div>
                <h2>Выступления</h2>
                <MaterialReactTable
                    displayColumnDefOptions={{
                        'mrt-row-actions': {
                            size: 60,
                        },
                    }}
                    columns={columns}
                    data={performances}
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
                            Добавить выступление
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

export default Performances;
