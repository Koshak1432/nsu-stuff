import React, {useCallback, useEffect, useMemo, useState} from 'react';
import {
    addContestDistribution,
    deleteContestDistribution,
    getContestsDistribution,
    updateContestDistribution
} from "../services/PerformanceService";
import {MaterialReactTable} from "material-react-table";
import {Box, Button, IconButton, Tooltip} from "@mui/material";
import {Delete, Edit} from "@mui/icons-material";
import CreateModal from "../components/UI/CreateModal";

const ContestsDistribution = () => {
    const columns = useMemo(() => [
        {
            header: "Имя",
            accessorKey: "artist.name",
            enableEditing: false,
        },
        {
            header: "Фамилия",
            accessorKey: "artist.surname",
            enableEditing: false,
        },
        {
            header: "Конкурс",
            accessorKey: "performance.name",
            enableEditing: false,
        },
        {
            header: "Место",
            accessorKey: "place",
        }
    ], []);

    const [distribution, setDistribution] = useState([]);
    const [createModalOpen, setCreateModalOpen] = useState(false);
    const [validationErrors, setValidationErrors] = useState({});


    const handleCreateNewRow = (values) => {
        const data = {
            id: {},
            artist: {
                name: values["artist.name"],
                surname: values["artist.surname"],
            },
            performance: {
                name: values["performance.name"],
            },
            place: values.place,
        };
        console.log(data);
        addContestDistribution(data).then(() => refreshData());
    };

    const handleSaveRowEdits = async ({exitEditingMode, row, values}) => {
        if (!Object.keys(validationErrors).length) {
            const data = {
                id: {},
                artist: {
                    name: values["artist.name"],
                    surname: values["artist.surname"],
                },
                performance: {
                    name: values["performance.name"],
                },
                place: values.place,
            };
            console.log(data);
            updateContestDistribution(data).then(() => refreshData());
            exitEditingMode(); //required to exit editing mode and close modal
        }
    };

    const handleDeleteRow = useCallback(
        (row) => {
            if (!confirm(`Are you sure?`)) {
                return;
            }
            const data = distribution[row.index].id;
            deleteContestDistribution(data).then(() => refreshData());
        },
        [],
    );

    const handleCancelRowEdits = () => {
        setValidationErrors({});
    };

    const refreshData = () => {
        getContestsDistribution().then(distribution => {
            setDistribution(distribution);
            console.log(distribution);
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
                    data={distribution}
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
                            Добавить связь конкурса
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

export default ContestsDistribution;
