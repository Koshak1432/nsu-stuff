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
            header: "ID артиста",
            accessorKey: "id.artistId",
            enableEditing: false,
        },
        {
            header: "ID конкурса",
            accessorKey: "id.performanceId",
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
        addContestDistribution({...values, id: 0}).then(() => refreshData());
    };

    const handleSaveRowEdits = async ({exitEditingMode, row, values}) => {
        if (!Object.keys(validationErrors).length) {
            distribution[row.index] = values;
            console.log(values);
            updateContestDistribution(values).then(() => refreshData());
            exitEditingMode(); //required to exit editing mode and close modal
        }
    };

    const handleDeleteRow = useCallback(
        (row) => {
            if (!confirm(`Are you sure you want to delete ${row.getValue("name")} ${row.getValue("surname")}`)) {
                return;
            }
            deleteContestDistribution(row.getValue("id")).then(() => refreshData());
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
